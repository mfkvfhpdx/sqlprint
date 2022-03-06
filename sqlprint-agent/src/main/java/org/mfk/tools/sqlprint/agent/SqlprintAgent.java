package org.mfk.tools.sqlprint.agent;

import java.lang.instrument.Instrumentation;

public class SqlprintAgent {
    public static void premain(String agentArgs, Instrumentation inst) {

        inst.addTransformer(new AgentTransformer(), true);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new AgentTransformer(), true);
    }
}
