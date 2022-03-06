package org.mfk.tools.sqlprint.core.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MethodProxy {
    public Class clzz;
    public Map<String,Method> methodMaps=new HashMap<String, Method>();

    public MethodProxy(String className) throws Exception {
        clzz = Class.forName(className);

    }
    public void addMethod(String name,String methodName,Class<?>[] paramsTypes) throws Exception {
        Method method = clzz.getMethod(methodName,paramsTypes);
        methodMaps.put(name,method);
    }
    public <T> T invoke(Class<T> clazz, String name,Object... objs) throws Exception {
        return (T) methodMaps.get(name).invoke(null,objs);
    }
    public <T> T invoke(Object obj,Class<T> clazz, String name,Object... objs) throws Exception {
        return (T) methodMaps.get(name).invoke(obj,objs);
    }
    public void invoke(String name,Object... objs) throws Exception {
        methodMaps.get(name).invoke(null,objs);
    }
    public void invoke(Object obj,String name,Object... objs) throws Exception {
        methodMaps.get(name).invoke(obj,objs);
    }
    public Object newInstanceObj() throws Exception {
        Object obj=clzz.newInstance();
        return obj;
    }

}
