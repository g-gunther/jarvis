package com.gguproject.jarvis.core.ioc.context.bean.beanresolver;

import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;

import java.util.Optional;

public class OptionalBeanResolver implements BeanResolver {
	@Override
	public boolean accept(BeanResolverField field){
		return Optional.class.isAssignableFrom(field.getBeanType());
	}

	@Override
	public Object resolve(ApplicationContext applicationContext, BeanResolverField field) {
		Optional<?> beanToInject;

		if(field.hasAnnotation(Qualifier.class)) {
			beanToInject = applicationContext.getOptionalBean(field.getGenericType(), field.getAnnotation(Qualifier.class).get().value());
		} else {
			beanToInject = applicationContext.getOptionalBean(field.getGenericType());
		}

		return beanToInject;
	}
}
