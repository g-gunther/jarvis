package com.gguproject.jarvis.core.ioc.context.bean.beanresolver;

import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.ioc.utils.ContextException;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import java.util.Optional;

public class SingleBeanResolver implements BeanResolver {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SingleBeanResolver.class);

	@Override
	public boolean accept(BeanResolverField field){
		return true;
	}

	@Override
	public Object resolve(ApplicationContext applicationContext, BeanResolverField field) {
		Optional<?> beanToInject;

		if(field.hasAnnotation(Qualifier.class)) {
			String qualifier = field.getAnnotation(Qualifier.class).get().value();
			beanToInject = applicationContext.getOptionalBean(field.getBeanType(), qualifier);
			if(beanToInject.isEmpty()) {
				throw new ContextException("Can't found bean to inject: " + field.getBeanType() + " and qualifier: " + qualifier);
			}
		} else {
			beanToInject = applicationContext.getOptionalBean(field.getBeanType());
			if(beanToInject.isEmpty()) {
				throw new ContextException("Can't found bean to inject: " + field.getBeanType());
			}
		}

		return beanToInject.get();
	}
}
