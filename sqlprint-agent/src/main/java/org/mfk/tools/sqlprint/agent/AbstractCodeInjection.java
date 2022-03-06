package org.mfk.tools.sqlprint.agent;

import javassist.CtClass;
import javassist.CtMethod;

public abstract class AbstractCodeInjection implements ICodeInjection {
    protected static final String PRINT_METHOD = "org.mfk.tools.sqlprint.core.utils.SqlPrintHelper.printSql";

    public void insertBefore(CtClass ctClass, String methodName, String code) throws Exception {
        CtMethod convertToAbbr = ctClass.getDeclaredMethod(methodName);
        convertToAbbr.insertBefore("{" + code + "}");
    }

    public void insertBefore(CtClass ctClass, String[] methodNames, String code) throws Exception {
        for (int i = 0; i < methodNames.length; i++) {
            String methodName = methodNames[i];
            insertBefore(ctClass, methodName, code);
        }
    }

    public void insertBeforeCommon(CtClass ctClass, String methodName, String params) throws Exception {
        insertBeforeCommon(ctClass, new String[]{methodName}, params);
    }

    public void insertBeforeCommon(CtClass ctClass, String[] methodNames, String params) throws Exception {
        insertBefore(ctClass, methodNames, PRINT_METHOD + "(" + params + ");");
    }

    public byte[] injection(CtClass ctClass) throws Exception {
        // 返回字节码，并且detachCtClass对象
        byte[] byteCode = ctClass.toBytecode();
        //detach的意思是将内存中曾经被javassist加载过的对象移除，如果下次有需要在内存中找不到会重新走javassist加载
        ctClass.detach();
        return byteCode;
    }
}
