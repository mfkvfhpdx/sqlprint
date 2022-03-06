package org.mfk.tools.sqlprint.agent.impl;

import javassist.CtClass;
import org.mfk.tools.sqlprint.agent.AbstractCodeInjection;
import org.mfk.tools.sqlprint.agent.ICodeInjection;

public class MysqlPSCodeInjectionImpl extends AbstractCodeInjection implements ICodeInjection {
    private static final String[] METHOD_NAMES = new String[]{"executeInternal"};

    @Override
    public byte[] injection(CtClass ctClass) throws Exception {
        super.insertBeforeCommon(ctClass, METHOD_NAMES, "$0");
        return super.injection(ctClass);
    }
}
