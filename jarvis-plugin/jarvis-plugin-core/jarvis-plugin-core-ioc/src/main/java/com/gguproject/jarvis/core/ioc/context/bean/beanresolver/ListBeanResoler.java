package com.gguproject.jarvis.core.ioc.context.bean.beanresolver;

import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;

import java.util.List;

public class ListBeanResoler implements BeanResolver {
	@Override
	public boolean accept(BeanResolverField field){
		return field.isGeneric() && (java.util.ArrayList.class.isAssignableFrom(field.getBeanType()) || List.class.equals(field.getBeanType()));
	}

	@Override
	public Object resolve(ApplicationContext applicationContext, BeanResolverField field) {
		List<?> beansToInject;
		if(field.hasAnnotation(Qualifier.class)) {
			beansToInject = applicationContext.getBeans(field.getGenericType(), field.getAnnotation(Qualifier.class).get().value());
		} else {
			beansToInject = applicationContext.getBeans(field.getGenericType());
		}

		return beansToInject;
	}
}
