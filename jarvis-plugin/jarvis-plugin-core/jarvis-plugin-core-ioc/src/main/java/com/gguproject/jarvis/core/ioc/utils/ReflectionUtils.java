package com.gguproject.jarvis.core.ioc.utils;

import com.gguproject.jarvis.core.utils.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ReflectionUtils {

    /**
     * List all field having the given annotation
     *
     * @param annotationClass
     * @param clazz
     * @return
     */
    public static Collection<Field> findFieldsWithAnnotation(final Class<? extends Annotation> annotationClass, Class<?> clazz) {
        Set<Field> set = new HashSet<>();
        Class<?> c = clazz;
        while (c != null && c != Object.class) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass)) {
                    set.add(field);
                }
            }
            c = c.getSuperclass();
        }
        return set;
    }

    public static Collection<Method> findMethodsWithAnnotation(final Class<? extends Annotation> annotationClass, Class<?> clazz) {
        Set<Method> set = new HashSet<>();
        Class<?> c = clazz;
        while (c != null && c != Object.class) {
            for (Method method : c.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotationClass)) {
                    set.add(method);
                }
            }
            c = c.getSuperclass();
        }
        return set;
    }

    /**
     * Set a field value by reflection
     *
     * @param targetBean
     * @param targetField
     * @param value
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void setFieldValue(Object targetBean, Field targetField, Object value) throws IllegalArgumentException, IllegalAccessException {
        boolean accessible = targetField.isAccessible();
        targetField.setAccessible(true);
        targetField.set(targetBean, value);
        targetField.setAccessible(accessible);
    }

    /**
     * @param targetBean
     * @param value
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static void setFieldByType(Object targetBean, Class<?> fieldType, Object value) throws IllegalArgumentException, IllegalAccessException {
        Class<?> c = targetBean.getClass();
        while (c != null && c != Object.class) {
            for (Field field : c.getDeclaredFields()) {
                if (field.getType().isAssignableFrom(fieldType)) {
                    setFieldValue(targetBean, field, value);
                    return;
                }
            }
            c = c.getSuperclass();
        }
    }

    /**
     * Get all super class & interfaces of a given class
     *
     * @param childInterfaces
     * @return
     */
    public static Set<Class<?>> getAllSuperClass(Class<?>... childInterfaces) {
        Set<Class<?>> allInterfaces = new HashSet<>();
        for (int i = 0; i < childInterfaces.length; i++) {
            allInterfaces.add(childInterfaces[i]);
            allInterfaces.addAll(
                    getAllSuperClass(childInterfaces[i].getInterfaces())
            );
            if (childInterfaces[i].getSuperclass() != null && childInterfaces[i].getSuperclass() != Object.class) {
                allInterfaces.addAll(
                        getAllSuperClass(childInterfaces[i].getSuperclass())
                );
            }
        }
        return allInterfaces;
    }

    /**
     * @param annotationClass
     * @param clazz
     * @return
     */
    public static boolean hasAnnotation(final Class<? extends Annotation> annotationClass, Class<?> clazz) {
        return clazz.getAnnotation(annotationClass) != null;
    }

    /**
     * @param annotationClass
     * @param clazz
     * @return
     */
    public static <T extends Annotation> T getAnnotation(final Class<T> annotationClass, Class<?> clazz) {
        return (T) clazz.getAnnotation(annotationClass);
    }

    /**
     * @param annotationClass
     * @param clazz
     * @return
     */
    public static boolean hasAnnotation(final Class<? extends Annotation> annotationClass, Field field) {
        return field.getAnnotation(annotationClass) != null;
    }

    /**
     * @param annotationClass
     * @param clazz
     * @return
     */
    public static <T extends Annotation> T getAnnotation(final Class<T> annotationClass, Field field) {
        return (T) field.getAnnotation(annotationClass);
    }

    /**
     * @param field
     * @param type
     * @return
     */
    public static boolean isFieldOfType(Field field, Class<?> type) {
        return field.getType().equals(type) || ArrayUtils.contains(field.getType().getInterfaces(), type);
    }

    /**
     * @param instance
     * @param method
     * @throws ReflectionException
     */
    public static void callMethod(Object instance, String method) throws ReflectionException {
        callMethod(instance, method, new Class[]{}, new Object[]{});
    }

    /**
     * @param instance
     * @param method
     * @param parameterTypes
     * @param parameters
     * @throws ReflectionException
     */
    public static void callMethod(Object instance, String method, Class<?>[] parameterTypes, Object[] parameters) {
        callMethod(instance, instance.getClass(), method, parameterTypes, parameters);
    }

    /**
     * @param instance
     * @param method
     * @param parameterTypes
     * @param parameters
     * @throws ReflectionException
     */
    public static void callMethod(Object instance, Class<?> methodClass, String method, Class<?>[] parameterTypes, Object[] parameters) {
        try {
            Method launchMethod = methodClass.getDeclaredMethod(method, parameterTypes);
            launchMethod.setAccessible(true);
            launchMethod.invoke(instance, parameters);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ReflectionException("Can't invoke method " + method + " on object " + instance, e);
        }
    }
}
