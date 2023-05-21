package org.mfk.tools.sqlprint.agent.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class DefaultStatementMethodVisitor extends MethodVisitor implements Opcodes {
    private MethodVisitor methodVisitor;
    private int api;
    private String[] params;
    protected DefaultStatementMethodVisitor(int api, MethodVisitor methodVisitor,String[] params) {
        super(api, methodVisitor);
        this.methodVisitor = methodVisitor;
        this.api = api;
        this.params=params;
    }
    /**
     * 开始访问方法的代码（如果有的话，即非抽象方法）。
     */
    @Override
    public void visitCode() {
        super.visitCode();

        //只支持有一个或两个的参数
        if (params == null || params.length >2|| params.length==0){
            return;
        }
        for (int i = 0; i < params.length; i++) {
            String param=params[i];
            if (param.startsWith("$")){
                methodVisitor.visitVarInsn(ALOAD,Integer.parseInt(param.substring(1)) );
            }else{
                //暂不支持其他参数
                return;
            }
        }
        String methodDes = "";
        int length =params.length;
        if (length == 1){
            methodDes="(Ljava/sql/Statement;)V";
        }else if (length == 2){
            methodDes="(Ljava/sql/Statement;Ljava/lang/Object;)V";

        }
           methodVisitor.visitMethodInsn(INVOKESTATIC, "org/mfk/tools/sqlprint/core/utils/SqlPrintHelper", "printSql", methodDes, false);
    }

}
