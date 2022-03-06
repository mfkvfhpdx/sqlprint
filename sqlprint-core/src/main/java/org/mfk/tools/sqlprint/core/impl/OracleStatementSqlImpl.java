package org.mfk.tools.sqlprint.core.impl;

import org.mfk.tools.sqlprint.core.AbstractStatementSql;
import org.mfk.tools.sqlprint.core.IStatementSql;
import org.mfk.tools.sqlprint.core.utils.RefactUtils;

import java.lang.reflect.Array;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class OracleStatementSqlImpl extends AbstractStatementSql implements IStatementSql {
    Map<String, Object> valueCache = null;
    private static final String BASE_VALUE_FIELD = "parameter%s";

    public OracleStatementSqlImpl(Statement statement, Object otherparams) {
        super(statement, otherparams);
        // sql赋值
        sql = RefactUtils.getFieldValue(statement, "sqlObject.originalSql").toString();

        // 拼接字段赋值
        Object binds = RefactUtils.getFieldValue(statement, "currentRowBinders");
        if (binds != null) {
            int count = Array.getLength(binds);
            for (int i = 0; i < count; i++) {
                Object obj = Array.get(binds, i);
                String val = null;
                String className = obj.getClass().getName();
                if (obj == null){
                    val=converObjectSplicingString(val);
                    splicingParameters.add(val);
                    continue;
                }

                if (RefactUtils.getField(obj.getClass(), "paramVal") != null) {
                    val = converObjectSplicingString(RefactUtils.getFieldValue(obj, "paramVal"));
                } else {
                    //兼容ojdbc14
                    if (valueCache == null) {
                        valueCache = new HashMap<String, Object>();
                    }
                    String valueName = className.replace("oracle.jdbc.driver.", "").replace("Binder", "");
                    Object valueAss = null;
                    if (valueCache.get(valueName) != null) {
                        valueAss = valueCache.get(valueName);
                    } else {
                        valueAss = RefactUtils.getFieldValue(statement, String.format(BASE_VALUE_FIELD, valueName));
                        if (valueAss != null) {
                            valueAss = Array.get(valueAss, 0);
                        }
                    }

                    if (valueAss != null) {
                        val = converObjectSplicingString(Array.get(valueAss, i));
                    }else{
                        val = "null";
                    }
                }

                splicingParameters.add(val);
            }
        }

    }

    protected String converDateToSqlString(Date date) {
        return "to_date('" + defaultDateFormat.format(date) + "','yyyy-mm-dd hh24:mi:ss')";
    }

}
