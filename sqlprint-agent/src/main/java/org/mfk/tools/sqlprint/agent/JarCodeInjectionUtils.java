package org.mfk.tools.sqlprint.agent;

import javassist.ClassPool;
import javassist.CtClass;

import java.util.List;

public class JarCodeInjectionUtils {
    public static void main(String[] args) {

        injection("C:\\Users\\Administrator\\Desktop\\dev");

       // injection("file:\\D:\\dev\\workspace\\idea\\sqlprint\\lib\\ojdbc8-19.3.0.0_nosealed.jar");
    }

    public static void injection(String path) {


        try {

           // URLClassLoader classLoader=new URLClassLoader(new URL[]{new URL(jarPath)});
            ClassPool pool = new ClassPool(true);
            List<String> keys = AgentConfig.getCodeInjectionRegKeys();
            for (int i = 0; i < keys.size(); i++) {
                String targetClassName = keys.get(i).replaceAll("/", ".");
                CtClass ctClass = pool.getOrNull(targetClassName);
                if (ctClass==null){
                    continue;
                }

                if (targetClassName!=null){
                    try {
                        CodeInjectionFactory.createCodeInjection(keys.get(i)).injection(ctClass);
                        ctClass.writeFile(path);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
