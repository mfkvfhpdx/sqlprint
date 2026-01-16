package org.mfk.tools.sqlprint.core;

public class SpConfig {
    /**
     * 输出文件路径，null 表示不输出
     */
    private String outputPath;

    /**
     * 允许输出的最大堆栈数量，<=0 表示无限制
     */
    private int maxStackTraces = 100;

    /**
     * 包含过滤规则（白名单），支持通配符 *，如 "com.example.*"。
     * 如果为空或长度为0，则不过滤（全部允许）
     */
    private String[] includePatterns = {};

    /**
     * 排除过滤规则（黑名单），支持通配符 *，如 "sun.*", "java.lang.*"。
     * 在 include 之后应用
     */
    private String[] excludePatterns = {};

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public int getMaxStackTraces() {
        return maxStackTraces;
    }

    public void setMaxStackTraces(int maxStackTraces) {
        this.maxStackTraces = maxStackTraces;
    }

    public String[] getIncludePatterns() {
        return includePatterns;
    }

    public void setIncludePatterns(String[] includePatterns) {
        this.includePatterns = includePatterns;
    }

    public String[] getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(String[] excludePatterns) {
        this.excludePatterns = excludePatterns;
    }
}
