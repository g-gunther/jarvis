package com.gguproject.jarvis.core.ioc.context.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.ioc.context.bean.BeanContext;
import com.gguproject.jarvis.core.ioc.context.bean.SingletonBeanContext;
import com.gguproject.jarvis.core.ioc.utils.ContextException;

public class SingletonBeanContextBuilder extends BeanContextBuilder {
	
	public SingletonBeanContextBuilder(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	@Override
	public BeanContext build(Class<?> beanType) {
		return new SingletonBeanContext(beanType, this.getOrder(), this.applicationContext);
	}

}
