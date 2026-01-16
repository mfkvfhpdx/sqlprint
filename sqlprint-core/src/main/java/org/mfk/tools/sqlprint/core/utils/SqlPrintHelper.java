package org.mfk.tools.sqlprint.core.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Statement;


import org.mfk.tools.sqlprint.core.SpConfig;
import org.mfk.tools.sqlprint.core.StatementSqlFactory;
import org.mfk.tools.sqlprint.core.print.ISqlPrinter;
import org.mfk.tools.sqlprint.core.print.impl.DefaultSqlPrinterImpl;

public class SqlPrintHelper {
    private static ISqlPrinter iSqlPrinter = null;

    public static void setSqlPrinter(ISqlPrinter iSqlPrinter) {
        SqlPrintHelper.iSqlPrinter = iSqlPrinter;
    }

    public static void printSql(Statement statement) {
        printSql(statement, null);
    }

    /**
     * @param pkgPrefix               数据库执行sql过程中，statement所在包
     * @param statementSqlMapperClazz 对应数据库statement的sql提取实现
     */
    public static void addStatementSqlImpl(String pkgPrefix, Class statementSqlMapperClazz) {
        StatementSqlFactory.addStatementSqlImpl(pkgPrefix, statementSqlMapperClazz);
    }
    private static boolean init = false;
    public static SpConfig spConfig = null;
    public static void init(){
        if (init) return;
        init=true;
        Runtime.getRuntime().addShutdownHook(new Thread(RobustLogFileWriter::closeAllWriters));
        String agentStr =  System.getProperty("sqlprint.agent.message");
        if (agentStr!=null) {
            String[] _args = agentStr.split(";");
            for (String arg : _args) {
                if (arg.startsWith("config:")) {
                    spConfig = new SpConfig();
                    String configStr = arg.substring("config:".length());
                    String[] configs = configStr.split("&");
                    for (String config : configs) {
                        String[] kv = config.split("=");
                        if (kv.length == 2) {
                            String key = kv[0].trim();
                            String value = kv[1].trim();
                            if (value==null) value = "";
                            if (key.equals("outputPath")) {

                                spConfig.setOutputPath(value);
                            } else if (key.equals("maxStackTraces")) {
                                spConfig.setMaxStackTraces(Integer.parseInt(value));
                            } else if (key.equals("includePatterns")) {
                                spConfig.setIncludePatterns(value.split(","));
                            } else if (key.equals("excludePatterns")) {
                                spConfig.setExcludePatterns(value.split(","));
                            }

                        }

                    }
                }
            }

        }
    }
    public static void printSql(Statement statement, Object object) {
        try {
            init();
            if (iSqlPrinter == null) {
                iSqlPrinter = new DefaultSqlPrinterImpl();
            }
            iSqlPrinter.printSql(statement, object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将字符串追加写入指定文件（UTF-8 编码）
     *
     * @param outputPath 文件输出路径，不能为 null 或空
     * @param string     要写入的字符串，可为 null（此时不写入任何内容）
     */
    public static void writeToFile(String outputPath, String string) {
        RobustLogFileWriter.writeToFile(outputPath, string);
    }
}
