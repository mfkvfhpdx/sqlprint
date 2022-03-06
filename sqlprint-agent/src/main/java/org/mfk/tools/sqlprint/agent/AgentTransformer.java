package org.mfk.tools.sqlprint.agent;

import javassist.ClassPool;
import javassist.CtClass;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class AgentTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (AgentConfig.isRegister(className)) {

            String targetClassName = className.replaceAll("/", ".");
            ICodeInjection icg = null;
            try {
                icg = CodeInjectionFactory.createCodeInjection(className);
                final ClassPool classPool = ClassPool.getDefault();
                final CtClass clazz = classPool.get(targetClassName);
                return icg.injection(clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;

    }
}
