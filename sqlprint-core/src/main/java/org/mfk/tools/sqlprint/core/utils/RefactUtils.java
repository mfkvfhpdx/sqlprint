package org.mfk.tools.sqlprint.core.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;


public class RefactUtils {
	public static void main(String[] args) {
		invokeStaticMethod("com.kingbase8.util.ByteConverter","int2",new Class[]{byte[].class,int.class} ,new byte[]{1,0},1);
	}

	public static Object invokeMethod(Method method) {

		return null;
	}
	public static Object invokeStaticMethod(String className,String methodName,Class<?>[] paramsTypes,Object... values) {
		try {
			Class clzz = Class.forName(className);
			Method method = clzz.getMethod(methodName,paramsTypes);
			return method.invoke(null,values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * jdk6 method没有getParameterCount,用这个方法代替
	 * @return
	 */
	public static int getMethodParameterCount(Method method) {
		int res = 0;
		try {
			Field field = method.getClass().getDeclaredField("parameterTypes");
			field.setAccessible(true);
			Class[] fs = (Class[]) field.get(method);
			res = fs.length;
		} catch (Exception e) {
			new RuntimeException(e);
		}

		return res;

	}

	public static Object getFieldValue(Object obj, String path) {

		Object tempObj = obj;
		String[] ps = path.split("\\.");
		List<String> ls = new ArrayList<String>();
		for (int i = 0; i < ps.length; i++) {
			String name = ps[i];
			Field f = getField(tempObj.getClass(), name);
			if (f == null) {
				return null;
			}
			f.setAccessible(true);
			try {
				tempObj = f.get(tempObj);
			} catch (Exception e) {

				return null;
			}
			if (tempObj == null) {
				return null;
			}
			// System.out.println(f.getName());
		}

		return tempObj;
	}

	public static Field getField(Class clazz, String fieldName) {
		if (clazz == null) {
			return null;
		}
		Field[] fs = clazz.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			String name = fs[i].getName();

			if (fieldName.equals(name)) {
				return fs[i];
			}
		}

		return getField(clazz.getSuperclass(), fieldName);
	}

	/**
	 * 获取一个class类的实际位置
	 * @param cls
	 * @return
	 */
	public static URL getClassLocation(final Class<?> cls) {

		//非空判断
		if (cls == null) {
			throw new IllegalArgumentException("null input: cls");
		}


		URL result = null;
		final String clsAsResource = cls.getName().replace('.', '/').concat(".class");

		final ProtectionDomain pd = cls.getProtectionDomain();

		if (pd != null) {
			final CodeSource cs = pd.getCodeSource();
			if (cs != null) {
				result = cs.getLocation();
			}

			if (result != null) {
				if ("file".equals(result.getProtocol())) {
					try {
						if (result.toExternalForm().endsWith(".jar") || result.toExternalForm().endsWith(".zip")) {
							result = new URL("jar:".concat(result.toExternalForm()).concat("!/").concat(clsAsResource));
						} else if (new File(result.getFile()).isDirectory()) {
							result = new URL(result, clsAsResource);
						}
					} catch (MalformedURLException ignore) {

					}
				}
			}
		}

		if (result == null) {
			final ClassLoader clsLoader = cls.getClassLoader();
			result = clsLoader != null ? clsLoader.getResource(clsAsResource) : ClassLoader.getSystemResource(clsAsResource);
		}

		return result;
	}
}
