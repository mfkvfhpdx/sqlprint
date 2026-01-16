package org.mfk.tools.sqlprint.agent;

import javassist.ClassPool;
import javassist.CtClass;
import org.mfk.tools.sqlprint.agent.utils.StringUtils;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class AgentTransformer implements ClassFileTransformer {
    private boolean debug=false;
    AgentTransformer(String mode){

        if (mode!=null){
            debug= StringUtils.containsDebug(mode);
        }

    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (debug&&className!=null&&(className.indexOf("oracle")>0||className.indexOf("mysql")>0||className.indexOf("sqlserver")>0||className.indexOf("kingbase8")>0)){
            System.out.println("SqlprintAgent:"+className);
        }
        if (AgentConfig.isRegister(className)) {

            String targetClassName = className.replaceAll("/", ".");
            ICodeInjection icg = null;
            try {
                icg = CodeInjectionFactory.createCodeInjection(className);
                byte[] resbytes=null;
                try {
                    if (debug){
                        System.out.println("使用asm进行注入:"+targetClassName);
                    }
                    resbytes=icg.injection(classfileBuffer);
                    if (debug){
                        System.out.println("asm注入成功:"+targetClassName);
                    }
                }catch (Throwable e) {
                    if (debug){
                        e.printStackTrace();
                        System.out.println("使用javassist进行注入:"+targetClassName);
                    }

                    final ClassPool classPool = ClassPool.getDefault();
                    final CtClass clazz = classPool.get(targetClassName);
                    return icg.injection(clazz);
                }
                return resbytes;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;

    }
}
