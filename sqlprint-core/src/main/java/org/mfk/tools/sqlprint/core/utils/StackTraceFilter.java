package org.mfk.tools.sqlprint.core.utils;

import org.mfk.tools.sqlprint.core.SpConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 堆栈跟踪过滤工具类
 */
public class StackTraceFilter {

    /**
     * 根据 SpConfig 配置过滤当前线程的调用堆栈，并返回匹配的 StackTraceElement 列表。
     *
     * @param config 过滤配置（不可为 null）
     * @return 过滤并截断后的堆栈元素列表
     */
    public static List<StackTraceElement> getFilteredStackTrace(SpConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("SpConfig must not be null");
        }

        StackTraceElement[] rawStack = Thread.currentThread().getStackTrace();
        // 跳过: [0] getStackTrace, [1] getFilteredStackTrace, [2] 调用者（即用户代码开始）
        List<StackTraceElement> result = new ArrayList<>();

        for (int i = 2; i < rawStack.length; i++) {
            StackTraceElement element = rawStack[i];
            String className = element.getClassName();

            // 白名单检查（如果 includePatterns 非空且非零长度）
            if (config.getIncludePatterns() != null && config.getIncludePatterns().length > 0) {
                boolean includeMatched = false;
                for (String pattern : config.getIncludePatterns()) {
                    if (wildcardMatch(className, pattern)) {
                        includeMatched = true;
                        break;
                    }
                }
                if (!includeMatched) {
                    continue;
                }
            }

            // 黑名单检查
            if (config.getExcludePatterns() != null) {
                boolean excludeMatched = false;
                for (String pattern : config.getExcludePatterns()) {
                    if (wildcardMatch(className, pattern)) {
                        excludeMatched = true;
                        break;
                    }
                }
                if (excludeMatched) {
                    continue;
                }
            }

            result.add(element);
        }

        // 应用最大数量限制
        int max = config.getMaxStackTraces();
        if (max > 0 && result.size() > max) {
            return result.subList(0, max);
        }

        return new ArrayList<>(result); // 返回可变副本（避免外部修改内部状态）
    }

    /**
     * 将过滤后的堆栈转换为标准格式的字符串（每行以 "\tat " 开头，末尾带换行）
     *
     * @param config 配置
     * @return 格式化后的堆栈字符串，若无匹配则返回空字符串
     */
    public static String getFilteredStackTraceAsString(SpConfig config) {
        List<StackTraceElement> elements = getFilteredStackTrace(config);
        if (elements.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : elements) {
            sb.append("\tat ").append(e.toString()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * 支持 Ant 风格通配符：
     * - '*'  匹配单个点分段（不包含 '.'）
     * - '**' 匹配任意多段（可包含 '.'，跨段）
     */
    public static boolean wildcardMatch(String text, String pattern) {
        if (text == null || pattern == null) {
            return false;
        }
        if ("**".equals(pattern)) {
            return true;
        }

        String regex = convertToRegex(pattern);
        return Pattern.matches(regex, text);
    }

    private static String convertToRegex(String pattern) {
        StringBuilder sb = new StringBuilder();
        sb.append("^");

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);

            if (c == '*') {
                // 处理 **
                if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '*') {
                    sb.append(".*");
                    i++; // 跳过下一个 *
                } else {
                    // 单个 *
                    sb.append("[^.]*");
                }
            } else {
                // 转义正则特殊字符
                if ("\\.[]{}()+-^$|?".indexOf(c) >= 0) {
                    sb.append("\\");
                }
                sb.append(c);
            }
        }

        sb.append("$");
        return sb.toString();
    }


    public static void main(String[] args) {
        System.out.println(wildcardMatch("com.gitee.dbswitch.admin.AdminApplication","com.*.dbswitch.**"));
    }
}