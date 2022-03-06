package org.mfk.tools.sqlprint.agent;

import javassist.CtClass;

public interface ICodeInjection {

    public byte[] injection(CtClass ctClass) throws Exception;
}
