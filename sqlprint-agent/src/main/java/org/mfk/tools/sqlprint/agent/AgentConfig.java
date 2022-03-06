package org.mfk.tools.sqlprint.agent;

import org.mfk.tools.sqlprint.agent.impl.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentConfig {
    public static final Map<String, Class> CODE_INJECTION_MAPPER = new HashMap<String, Class>();

    static {
        CODE_INJECTION_MAPPER.put("oracle/jdbc/driver/OracleStatement", OracleStCodeInjectionImpl.class);
        CODE_INJECTION_MAPPER.put("com/microsoft/sqlserver/jdbc/SQLServerStatement", SQLServerStCodeInjectionImpl.class);
        CODE_INJECTION_MAPPER.put("com/mysql/cj/jdbc/ClientPreparedStatement", MysqlCjPSCodeInjectionImpl.class);
        CODE_INJECTION_MAPPER.put("com/mysql/cj/jdbc/StatementImpl", MysqlCjStCodeInjectionImpl.class);
        CODE_INJECTION_MAPPER.put("com/mysql/jdbc/PreparedStatement", MysqlPSCodeInjectionImpl.class);
        CODE_INJECTION_MAPPER.put("com/mysql/jdbc/StatementImpl", MysqlStCodeInjectionImpl.class);
        CODE_INJECTION_MAPPER.put("com/kingbase8/jdbc/KbStatement", Kingbase8StCodeInjectionImpl.class);
    }

    public static Class getCodeInjectionImpl(String key) {
        return CODE_INJECTION_MAPPER.get(key);
    }

    public static void register(String key, Class<ICodeInjection> clazz) {
        CODE_INJECTION_MAPPER.put(key, clazz);
    }

    public static boolean isRegister(String key) {
        if (CODE_INJECTION_MAPPER.containsKey(key)) {
            return true;
        }
        return false;
    }

    public static List<String> getCodeInjectionRegKeys() {

        return new ArrayList<String>(CODE_INJECTION_MAPPER.keySet());
    }
}

