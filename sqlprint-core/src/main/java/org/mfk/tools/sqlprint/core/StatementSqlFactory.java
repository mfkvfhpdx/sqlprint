package org.mfk.tools.sqlprint.core;

import org.mfk.tools.sqlprint.core.impl.Kingbase8StatementSqlImpl;
import org.mfk.tools.sqlprint.core.impl.MysqlStatementSqlImpl;
import org.mfk.tools.sqlprint.core.impl.OracleStatementSqlImpl;
import org.mfk.tools.sqlprint.core.impl.SQLServerStatementSqlImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class StatementSqlFactory {
    private static final Map<String, Class> STATEMENT_SQL_MAPPER = new HashMap<String, Class>();

    static {

        STATEMENT_SQL_MAPPER.put("oracle.jdbc.driver", OracleStatementSqlImpl.class);
        STATEMENT_SQL_MAPPER.put("com.microsoft.sqlserver.jdbc", SQLServerStatementSqlImpl.class);
        STATEMENT_SQL_MAPPER.put("com.mysql.cj.jdbc", MysqlStatementSqlImpl.class);
        STATEMENT_SQL_MAPPER.put("com.mysql.jdbc", MysqlStatementSqlImpl.class);
        STATEMENT_SQL_MAPPER.put("com.kingbase8.jdbc",Kingbase8StatementSqlImpl.class);

    }

    public static IStatementSql createStatementSql(Statement statement) throws NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {

        return createStatementSql(statement, null);
    }

    public static IStatementSql createStatementSql(Statement statement, Object otherParams)
            throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        Iterator<Map.Entry<String, Class>> iterator = STATEMENT_SQL_MAPPER.entrySet().iterator();
        Class clazz = null;
        while (iterator.hasNext()){
            Map.Entry<String, Class> entry = iterator.next();
            if (statement.getClass().getName().indexOf(entry.getKey())==0)
            {
                clazz = entry.getValue();
                break;
            }

        }

        if (clazz == null) {
            throw new RuntimeException("?????????" + statement.getClass().getName() + "???????????????????????????");
        }

        Constructor con = clazz.getConstructor(Statement.class, Object.class);
        return (IStatementSql) con.newInstance(statement, otherParams);
    }


    /**
     *
     * @param className ???????????????sql???????????????????????????
     * @param statementSqlMapperClazz ???????????????statement???sql????????????
     */
    public static void addStatementSqlImpl(String className, Class statementSqlMapperClazz) {
        STATEMENT_SQL_MAPPER.put(className, statementSqlMapperClazz);
    }

}
