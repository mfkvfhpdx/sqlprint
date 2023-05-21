package org.mfk.tools.sqlprint.agent.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class DefaultStatementVisitor  extends ClassVisitor implements Opcodes {
    private ClassWriter writer;
    private int api;
    private String[] params;
    private String[] methodNames;
    public DefaultStatementVisitor(int api, ClassWriter writer,String[] methodNames,String[] params) {
        super(api, writer);
        this.writer = writer;
        this.api = api;
        this.params=params;
        this.methodNames = methodNames;
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        boolean flag= false;
        for (int i = 0; i < methodNames.length; i++) {
            if (name.equals(methodNames[i])) {
                flag=true;
                break;
            }
        }
        if (!flag){
            return mv;
        }
        DefaultStatementMethodVisitor methodVisitor = new DefaultStatementMethodVisitor(api,mv,params);
        return methodVisitor;
    }
    public byte[] toByteArray() {
        return writer.toByteArray();
    }
}
