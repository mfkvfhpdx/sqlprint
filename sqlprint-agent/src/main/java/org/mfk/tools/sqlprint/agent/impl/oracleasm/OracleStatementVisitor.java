package org.mfk.tools.sqlprint.agent.impl.oracleasm;

import org.mfk.tools.sqlprint.agent.impl.OracleStCodeInjectionImpl;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class OracleStatementVisitor  extends ClassVisitor implements Opcodes {
        private ClassWriter writer;
    private static final String[] METHOD_NAMES = new String[]{"prepareForNewResults"};

        public  OracleStatementVisitor(int api, ClassWriter writer) {
            super(api, writer);
            this.writer = writer;
        }
        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            boolean flag= false;
            for (int i = 0; i < METHOD_NAMES.length; i++) {
                if (name.equals(METHOD_NAMES[i])) {
                    flag=true;
                    break;
                }
            }
            if (!flag){
                return mv;
            }
            OracleStatementMethodVisitor methodVisitor = new OracleStatementMethodVisitor(api,mv);
            return methodVisitor;
        }
        public byte[] toByteArray() {
            return writer.toByteArray();
        }
    }