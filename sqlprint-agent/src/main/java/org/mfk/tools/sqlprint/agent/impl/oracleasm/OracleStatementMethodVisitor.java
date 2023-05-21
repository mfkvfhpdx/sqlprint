package org.mfk.tools.sqlprint.agent.impl.oracleasm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class OracleStatementMethodVisitor extends MethodVisitor implements Opcodes {
       private MethodVisitor methodVisitor;
        protected OracleStatementMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
            this.methodVisitor=methodVisitor;
        }
        @Override
        public void visitCode() {
            super.visitCode();
            methodVisitor.visitVarInsn(ILOAD, 1);
            Label label0 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "org/mfk/tools/sqlprint/core/utils/SqlPrintHelper", "printSql", "(Ljava/sql/Statement;)V", false);
            methodVisitor.visitLabel(label0);

        }
    }