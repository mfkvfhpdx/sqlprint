package org.mfk.tools.sqlprint.agent;

import java.lang.instrument.Instrumentation;

public class SqlprintAgent {
    public static void premain(String agentArgs, Instrumentation inst) {

        startAget(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {

        startAget(agentArgs, inst);
    }

    public static void startAget(String agentArgs, Instrumentation inst) {
        if (agentArgs != null) {
            if (agentArgs.indexOf("debug") >= 0) {
                System.out.println("sqlprint start!");
            }

        }
        if (agentArgs == null){
            agentArgs= "";
        }
        inst.addTransformer(new AgentTransformer(agentArgs), true);
        if (agentArgs.indexOf("debug") >= 0) {
            System.out.println("sqlprint end!");
        }
    }
}
