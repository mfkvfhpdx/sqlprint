package org.mfk.tools.sqlprint.agent;

import javassist.CtClass;

public interface ICodeInjection {

    public byte[] injection(Object obj) throws Exception;

}
