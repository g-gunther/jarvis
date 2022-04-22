package com.gguproject.jarvis.core.ioc.context.builder;

import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.ioc.context.annotation.Order;
import com.gguproject.jarvis.core.ioc.context.bean.BeanContext;
import com.gguproject.jarvis.core.ioc.utils.ContextException;
import com.gguproject.jarvis.core.ioc.utils.ReflectionUtils;

public abstract class BeanContextBuilder {
	
	protected ApplicationContext applicationContext;
	
	private int order = 0;
	
	public BeanContextBuilder(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public BeanContext buildBeanContext(Class<?> beanType) throws ContextException {
		if(ReflectionUtils.hasAnnotation(Order.class, beanType)) {
			Order orderAnnotation = ReflectionUtils.getAnnotation(Order.class, beanType);
			this.order = orderAnnotation.value();
		}
		
		return this.build(beanType);
	}
	
	public abstract BeanContext build(Class<?> beanType) throws ContextException;
	
	public int getOrder() {
		return this.order;
	}
}
