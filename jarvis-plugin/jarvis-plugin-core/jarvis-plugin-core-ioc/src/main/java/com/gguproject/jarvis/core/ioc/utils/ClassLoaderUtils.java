package com.gguproject.jarvis.core.ioc.utils;

public class ClassLoaderUtils {

	@SuppressWarnings("unchecked")
	public static <T> T loadClass(ClassLoader classLoader, String className) {
		try {
			return (T) classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new ClassLoaderException("Can't load class " + className + " in classLoader " + classLoader, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T instantiate(ClassLoader classLoader, String className) {
		Class<?> clazz = loadClass(classLoader, className);
		try {
			return (T) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ClassLoaderException("Can't instantiate class " + className + " in classLoader " + classLoader, e);
		}
	}
}
