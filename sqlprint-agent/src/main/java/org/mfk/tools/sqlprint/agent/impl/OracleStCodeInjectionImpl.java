package org.mfk.tools.sqlprint.agent.impl;

import javassist.CtClass;
import org.mfk.tools.sqlprint.agent.AbstractCodeInjection;
import org.mfk.tools.sqlprint.agent.ICodeInjection;

public class OracleStCodeInjectionImpl extends AbstractCodeInjection implements ICodeInjection {
    private static final String[] METHOD_NAMES = new String[]{"prepareForNewResults"};

    @Override
    public byte[] injection(CtClass ctClass) throws Exception {
        super.insertBefore(ctClass, METHOD_NAMES, "{if($1){" + PRINT_METHOD + "($0);}}");
        return super.injection(ctClass);
    }
}
