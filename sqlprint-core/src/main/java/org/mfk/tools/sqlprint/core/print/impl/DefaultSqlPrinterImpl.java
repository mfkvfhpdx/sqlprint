package org.mfk.tools.sqlprint.core.print.impl;

import org.mfk.tools.sqlprint.core.IStatementSql;
import org.mfk.tools.sqlprint.core.SpConfig;
import org.mfk.tools.sqlprint.core.print.AbstractSqlPrinter;
import org.mfk.tools.sqlprint.core.print.ISqlPrinter;
import org.mfk.tools.sqlprint.core.utils.SqlPrintHelper;
import org.mfk.tools.sqlprint.core.utils.StackTraceFilter;

import java.sql.Statement;
import java.time.format.DateTimeFormatter;


public class DefaultSqlPrinterImpl extends AbstractSqlPrinter implements ISqlPrinter {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override
    public void printSql(Statement statement, Object otherParams) throws Exception {
        SpConfig spConfig = SqlPrintHelper.spConfig;
        IStatementSql isql = getStatementSql(statement, otherParams);
        String formatSql = getFormatSql(isql);
        StringBuffer sb = new StringBuffer();
        sb.append(formatter.format(java.time.LocalDateTime.now())+":" + LINE);
        sb.append("------------" + statement.getClass().getSimpleName() + "DriverSql Start-----------" + LINE);
        sb.append(formatSql + LINE);
        sb.append("------------" + statement.getClass().getSimpleName() + "DriverSql End-------" + LINE);
        //sb.append("SqlParameters:" + StringUtilsExt.concatArraysToString(csarray, ",", "", "", 0) + LINE);
        if (spConfig != null) {
            sb.append(StackTraceFilter.getFilteredStackTraceAsString(spConfig));
            if (spConfig.getOutputPath() != null && !spConfig.getOutputPath().equals("")) {
                SqlPrintHelper.writeToFile(spConfig.getOutputPath(), sb.toString());
            }
        }

        System.out.println(sb);

    }

}
