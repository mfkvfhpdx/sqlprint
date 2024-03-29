package org.mfk.tools.sqlprint.agent.impl;

import javassist.CtClass;
import org.mfk.tools.sqlprint.agent.AbstractCodeInjection;
import org.mfk.tools.sqlprint.agent.ICodeInjection;

public class Kingbase8StCodeInjectionImpl extends AbstractCodeInjection implements ICodeInjection {
    private static final String[] METHOD_NAMES = new String[]{"executeInternal"};

    @Override
    public byte[] injection(CtClass ctClass) throws Exception {
        super.insertBeforeCommon(ctClass, METHOD_NAMES, "$0,$1");
        return super.injection(ctClass);
    }
    @Override
    public byte[] injection(byte[] bytes) throws Exception {
        return super.injectionAsm(bytes,METHOD_NAMES,new String[]{"$0","$1"});
    }
}
