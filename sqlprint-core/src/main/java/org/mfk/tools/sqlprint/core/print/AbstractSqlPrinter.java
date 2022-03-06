package org.mfk.tools.sqlprint.core.print;

import org.mfk.tools.sqlprint.core.IStatementSql;
import org.mfk.tools.sqlprint.core.StatementSqlFactory;
import org.mfk.tools.sqlprint.core.utils.SqlStringUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;


public abstract class AbstractSqlPrinter implements ISqlPrinter {
    protected final static String LINE = "\n";

    public String getFormatSql(IStatementSql statementSql) {
        String formatSql = SqlStringUtils.setSqlParameters(statementSql.getSql(), statementSql.getSplicingParameters());
        return formatSql;
    }

    public IStatementSql getStatementSql(Statement statement, Object otherParams)
            throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        return StatementSqlFactory.createStatementSql(statement, otherParams);
    }

    public static String getStackTrace(){
        StringBuffer sb=new StringBuffer();
        int level=3;
        int count=0;
        int maxLine=100;
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            if (i<level) {
                continue;
            }
            StackTraceElement s=stackTrace[i];
            String str=s.toString();
            if (str.indexOf(".java")>10) {
                sb.append(str+"\n");
                count++;
                if (maxLine==count) {
                    break;
                }
            }

        }
        return sb.toString();
    }
}
