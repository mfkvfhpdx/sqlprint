package org.mfk.tools.sqlprint.core.utils;

import java.sql.Statement;


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
     * @param className               数据库执行sql过程中，实际的类名
     * @param statementSqlMapperClazz 对应数据库statement的sql提取实现
     */
    public static void addStatementSqlImpl(String className, Class statementSqlMapperClazz) {
        StatementSqlFactory.addStatementSqlImpl(className, statementSqlMapperClazz);
    }

    public static void printSql(Statement statement, Object object) {
        try {
            if (iSqlPrinter == null) {
                iSqlPrinter = new DefaultSqlPrinterImpl();
            }
            iSqlPrinter.printSql(statement, object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
