package org.mfk.tools.sqlprint.core.impl;

import org.mfk.tools.sqlprint.core.AbstractStatementSql;
import org.mfk.tools.sqlprint.core.IStatementSql;
import org.mfk.tools.sqlprint.core.utils.RefactUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;

public class MysqlStatementSqlImpl extends AbstractStatementSql implements IStatementSql {

    public MysqlStatementSqlImpl(Statement statement, Object otherparams) {
        super(statement, otherparams);
        boolean mysql8ps = false;
        if ("com.mysql.cj.jdbc.ClientPreparedStatement".equals(statement.getClass().getName())) {
            mysql8ps = true;
        }
        // originalSql
        if (otherparams != null) {
            sql = otherparams.toString();
        } else {
            String path = "originalSql";
            if (mysql8ps) {
                path = "query.originalSql";
            }

            sql = RefactUtils.getFieldValue(statement, path).toString();
        }

        if (mysql8ps) {
            Object bindValues = RefactUtils.getFieldValue(statement, "query.queryBindings.bindValues");
            String charEncoding = RefactUtils.getFieldValue(statement, "query.charEncoding").toString();
            if (bindValues != null) {
                int len = Array.getLength(bindValues);
                for (int i = 0; i < len; i++) {
                    String value = null;
                    Object bindValue = Array.get(bindValues, i);
                    Object objval = RefactUtils.getFieldValue(bindValue, "value");
                    if (objval instanceof byte[]) {
                        try {
                            value = new String((byte[]) objval, charEncoding);
                            splicingParameters.add(value);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }else {
                        splicingParameters.add(converObjectSplicingString(objval));
                    }

                }
            }
        } else {
            Object objparameterTypes = RefactUtils.getFieldValue(statement, "parameterTypes");

            if (objparameterTypes != null) {
                byte[][] parameterValues = (byte[][]) RefactUtils.getFieldValue(statement, "parameterValues");
                int[] parameterTypes = (int[]) objparameterTypes;
                String charEncoding = RefactUtils.getFieldValue(statement, "charEncoding").toString();
                for (int i = 0; i < parameterTypes.length; i++) {
                    String value = null;
                    byte[] bs = parameterValues[i];
                    int type = parameterTypes[i];
                    switch (type) {
                        case Types.NULL:

                            break;

                        default:
                            try {
                                value = new String(bs, charEncoding);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    splicingParameters.add(value);
                }
            }
        }

    }

    protected String converDateToSqlString(Date date) {
        return "str_to_date('" + defaultDateFormat.format(date) + "','%Y-%m-%d %H:%i:%s')";
    }

}
