package org.mfk.tools.sqlprint.core.print.impl;

import org.mfk.tools.sqlprint.core.IStatementSql;
import org.mfk.tools.sqlprint.core.print.AbstractSqlPrinter;
import org.mfk.tools.sqlprint.core.print.ISqlPrinter;

import java.sql.Statement;


public class DefaultSqlPrinterImpl extends AbstractSqlPrinter implements ISqlPrinter {

    @Override
    public void printSql(Statement statement, Object otherParams) throws Exception {
        IStatementSql isql = getStatementSql(statement, otherParams);
        String formatSql = getFormatSql(isql);
        StringBuffer sb = new StringBuffer();
        sb.append("------------" + statement.getClass().getSimpleName() + "DriverSql Start-----------" + LINE);
        sb.append(formatSql + LINE);
        sb.append("------------" + statement.getClass().getSimpleName() + "DriverSql End-------" + LINE);
        //sb.append("SqlParameters:" + StringUtilsExt.concatArraysToString(csarray, ",", "", "", 0) + LINE);
        //sb.append(getStackTrace());
        System.out.println(sb);

    }

}
