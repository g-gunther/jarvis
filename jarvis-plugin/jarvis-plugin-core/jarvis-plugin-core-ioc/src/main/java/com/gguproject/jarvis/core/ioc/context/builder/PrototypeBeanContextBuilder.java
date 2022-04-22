package com.gguproject.jarvis.core.ioc.context.builder;

import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.ioc.context.bean.BeanContext;
import com.gguproject.jarvis.core.ioc.context.bean.PrototypeBeanContext;

public class PrototypeBeanContextBuilder extends BeanContextBuilder {

	public PrototypeBeanContextBuilder(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	@Override
	public BeanContext build(Class<?> beanType) {
		return new PrototypeBeanContext(beanType, this.getOrder(), this.applicationContext);
	}

}
