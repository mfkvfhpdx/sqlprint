package org.mfk.tools.sqlprint.core.impl;

import org.mfk.tools.sqlprint.core.AbstractStatementSql;
import org.mfk.tools.sqlprint.core.IStatementSql;
import org.mfk.tools.sqlprint.core.utils.RefactUtils;

import java.lang.reflect.Array;
import java.sql.Statement;
import java.util.Date;


public class SQLServerStatementSqlImpl extends AbstractStatementSql implements IStatementSql {

    public SQLServerStatementSqlImpl(Statement statement, Object otherparams) {
        super(statement, otherparams);

        // sql赋值
        if (statement.getClass().getName().equals("com.microsoft.sqlserver.jdbc.SQLServerStatement")) {

            sql = RefactUtils.getFieldValue(otherparams, "sql").toString();

        } else {

            sql = RefactUtils.getFieldValue(statement, "sqlCommand").toString();
        }

        // 拼接字段赋值
        Object inOutParam = RefactUtils.getFieldValue(statement, "inOutParam");
        if (inOutParam != null) {
            int count = Array.getLength(inOutParam);
            for (int i = 0; i < count; i++) {
                Object p = Array.get(inOutParam, i);
                String val = converObjectSplicingString(RefactUtils.getFieldValue(p, "inputDTV.impl.value"));
                splicingParameters.add(val);
            }
        }
    }

    protected String converDateToSqlString(Date date) {
        return "CONVERT(datetime,'" + defaultDateFormat.format(date) + "',20)";
    }
}
