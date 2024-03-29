package org.mfk.tools.sqlprint.agent.impl;

import javassist.CtClass;
import org.mfk.tools.sqlprint.agent.AbstractCodeInjection;
import org.mfk.tools.sqlprint.agent.ICodeInjection;

public class MysqlCjPSCodeInjectionImpl extends AbstractCodeInjection {
    private static final String[] METHOD_NAMES = new String[]{"executeInternal", "executeBatchInternal"};

    @Override
    public byte[] injection(CtClass ctClass) throws Exception {
        super.insertBeforeCommon(ctClass, METHOD_NAMES, "$0");
        return super.injection(ctClass);
    }
    @Override
    public byte[] injection(byte[] bytes) throws Exception {
        return super.injectionAsm(bytes,METHOD_NAMES,new String[]{"$0"});
    }
}
