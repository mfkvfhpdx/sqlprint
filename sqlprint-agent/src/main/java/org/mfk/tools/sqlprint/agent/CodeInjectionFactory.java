package org.mfk.tools.sqlprint.agent;

import java.lang.reflect.InvocationTargetException;

public class CodeInjectionFactory {

    public static ICodeInjection createCodeInjection(String codeInjectionName)
            throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Class clazz = AgentConfig.getCodeInjectionImpl(codeInjectionName);
        if (clazz == null) {
            throw new RuntimeException("未找到" + codeInjectionName + "所对应的代码注入实现！");
        }

        return (ICodeInjection) clazz.newInstance();
    }
}
