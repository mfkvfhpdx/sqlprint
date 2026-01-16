package org.mfk.tools.sqlprint.core.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobustLogFileWriter {

    private static final Map<String, BufferedWriter> writerCache = new ConcurrentHashMap<>();
    private static final Pattern SUFFIX_PATTERN = Pattern.compile("^(.*?)(?:_(\\d+))?(\\.[^.]*)?$");

    /**
     * 安全地将字符串追加写入日志文件。
     * - 若文件被占用（如 Windows 下被其他进程锁定），自动尝试 file_1.log, file_2.log...
     * - 自动创建父目录
     * - 线程安全，带 writer 缓存
     */
    public static void writeToFile(String originalPath, String content) {
        if (originalPath == null || originalPath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path must not be null or empty");
        }
        if (content == null) {
            return;
        }

        String basePath = originalPath.trim();
        String currentPath = basePath;
        int attempt = 0;
        final int maxAttempts = 10; // 防止无限循环

        while (attempt <= maxAttempts) {
            try {
                ensureParentDir(currentPath);
                BufferedWriter writer = getOrCreateWriter(currentPath);
                writer.write(content);
                writer.flush(); // 日志通常需立即落盘
                return; // 成功，退出
            } catch (IOException e) {
                // 判断是否是“文件被占用”错误（Windows 特有）
                if (isFileLockError(e) && attempt < maxAttempts) {
                    // 生成下一个备用文件名：file.log → file_1.log → file_2.log...
                    currentPath = getNextFallbackPath(basePath, attempt + 1);
                    attempt++;
                } else {
                    // 其他错误（如磁盘满、权限不足）直接报错
                    System.err.println("Failed to write log after " + attempt + " attempts: " + currentPath);
                    e.printStackTrace();
                    return;
                }
            }
        }

        System.err.println("Max retry attempts reached. Failed to write log to any fallback file.");
    }

    // ==================== Helper Methods ====================

    private static void ensureParentDir(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    private static BufferedWriter getOrCreateWriter(String path) throws IOException {
        return writerCache.computeIfAbsent(path, p -> {
            try {
                return Files.newBufferedWriter(
                        Paths.get(p),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            } catch (IOException e) {
                throw new RuntimeException(e); // 转为 unchecked，便于外层捕获
            }
        });
    }

    /**
     * 判断异常是否由“文件被其他进程占用”引起（主要针对 Windows）
     */
    private static boolean isFileLockError(IOException e) {
        String msg = e.getMessage();
        if (msg == null) return false;
        // Windows 常见错误信息
        return msg.contains("being used by another process") ||
                msg.contains("另一个程序正在使用此文件") ||
                msg.contains("The process cannot access the file");
    }

    /**
     * 生成 fallback 文件路径：
     *   "app.log"      → "app_1.log"
     *   "logs/app.txt" → "logs/app_1.txt"
     *   "data"         → "data_1"
     */
    private static String getNextFallbackPath(String originalPath, int suffix) {
        Matcher m = SUFFIX_PATTERN.matcher(originalPath);
        if (m.matches()) {
            String name = m.group(1); // 主体名
            String ext = m.group(3) != null ? m.group(3) : ""; // 扩展名（含 .）
            return name + "_" + suffix + ext;
        }
        return originalPath + "_" + suffix; // fallback
    }

    // ==================== Cleanup ====================

    /**
     * 关闭指定路径的 writer（用于 rotate 或重试前清理）
     */
    public static void closeWriter(String path) {
        if (path == null) return;
        BufferedWriter writer = writerCache.remove(path);
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("Error closing writer for: " + path);
                e.printStackTrace();
            }
        }
    }

    /**
     * 应用关闭时调用，释放所有文件句柄
     */
    public static void closeAllWriters() {
        for (String path : writerCache.keySet()) {
            closeWriter(path);
        }
    }
}