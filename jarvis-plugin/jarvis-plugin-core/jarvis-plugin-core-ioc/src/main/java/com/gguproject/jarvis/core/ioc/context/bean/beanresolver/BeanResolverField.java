package com.gguproject.jarvis.core.ioc.context.bean.beanresolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BeanResolverField {

    private Class<?> beanType;

    private boolean isGeneric = false;

    private Class<?> genericType;

    private List<Annotation> annotations;

    private BeanResolverField(Type beanType, List<Annotation> annotations) {
        this.annotations = annotations;

        if(beanType instanceof ParameterizedType) {
            this.isGeneric = true;
            this.beanType = (Class<?>) ((ParameterizedType) beanType).getRawType();
            this.genericType = this.determineGenericType(beanType);
        } else {
            this.beanType = (Class<?>) beanType;
        }
    }

    private Class<?> determineGenericType(Type beanType) {
        ParameterizedType parameterizedType = (ParameterizedType) beanType;
        if(parameterizedType.getActualTypeArguments()[0] instanceof ParameterizedType){
            return (Class<?>) ((ParameterizedType) parameterizedType.getActualTypeArguments()[0]).getRawType();
        } else {
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
    }

    public boolean isGeneric(){
        return this.isGeneric;
    }

    public Class<?> getBeanType(){
        return this.beanType;
    }

    public Class<?> getGenericType(){
        return this.genericType;
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return this.getAnnotation(annotation).isPresent();
    }

    public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotation) {
        return (Optional<T>) this.annotations.stream().filter(a -> a.annotationType().equals(annotation)).findFirst();
    }

    /**
     * From constructor parameter informations, build a list of field to inject
     * @param fieldAnnotations
     * @param fieldTypes
     * @return
     */
    public static List<BeanResolverField> from(Annotation[][] fieldAnnotations, Type[] fieldTypes){
        if(fieldAnnotations.length != fieldTypes.length) {
            throw new IllegalArgumentException("Field annotations and field types should have the same length");
        }

        List<BeanResolverField> fields = new ArrayList<>();
        for(int beanIndex = 0; beanIndex < fieldTypes.length; beanIndex++) {
            Type beanType = fieldTypes[beanIndex];
            List<Annotation> beanAnnotations = Arrays.asList(fieldAnnotations[beanIndex]);
            fields.add(new BeanResolverField(beanType, beanAnnotations));
        }

        return fields;
    }
}
