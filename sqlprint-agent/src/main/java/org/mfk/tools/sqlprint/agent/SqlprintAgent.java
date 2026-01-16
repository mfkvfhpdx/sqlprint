package org.mfk.tools.sqlprint.agent;

import org.mfk.tools.sqlprint.agent.utils.StringUtils;

import java.lang.instrument.Instrumentation;

public class SqlprintAgent {
    private static boolean debug=false;
    public static void premain(String agentArgs, Instrumentation inst) {
        System.setProperty("sqlprint.agent.message",agentArgs);
        startAget(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.setProperty("sqlprint.agent.message",agentArgs);
        startAget(agentArgs, inst);
    }

    public static void startAget(String agentArgs, Instrumentation inst) {
        debug = StringUtils.containsDebug(agentArgs);
        if (debug) {
            System.out.println("sqlprint start!");
            System.out.println("sqlprint.agent.message="+agentArgs);
        }

        if (agentArgs == null){
            agentArgs= "";
        }
        inst.addTransformer(new AgentTransformer(agentArgs), true);
        if (debug) {
            System.out.println("sqlprint end!");
        }
    }
}
