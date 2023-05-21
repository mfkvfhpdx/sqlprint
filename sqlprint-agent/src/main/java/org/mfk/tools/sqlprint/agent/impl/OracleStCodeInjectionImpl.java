package org.mfk.tools.sqlprint.agent.impl;

import javassist.CtClass;
import org.mfk.tools.sqlprint.agent.AbstractCodeInjection;
import org.mfk.tools.sqlprint.agent.ICodeInjection;
import org.mfk.tools.sqlprint.agent.impl.oracleasm.OracleStatementVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class OracleStCodeInjectionImpl extends AbstractCodeInjection implements ICodeInjection {
    private static final String[] METHOD_NAMES = new String[]{"prepareForNewResults"};

    @Override
    public byte[] injection(CtClass ctClass) throws Exception {
        super.insertBefore(ctClass, METHOD_NAMES, "{if($1){" + PRINT_METHOD + "($0);}}");
        return super.injection(ctClass);
    }
    @Override
    public byte[] injection(byte[] bytes) throws Exception {
        OracleStatementVisitor classVisitor=new  OracleStatementVisitor(Opcodes.ASM8,new ClassWriter(ClassWriter.COMPUTE_FRAMES));
        ClassReader classReader=new ClassReader(bytes);
        classReader.accept(classVisitor,ClassReader.EXPAND_FRAMES);
        byte[] data=classVisitor.toByteArray();
        return data;
    }





}
