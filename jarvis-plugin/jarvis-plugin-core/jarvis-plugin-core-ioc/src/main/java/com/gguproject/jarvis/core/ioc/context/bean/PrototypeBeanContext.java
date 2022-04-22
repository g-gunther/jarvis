package com.gguproject.jarvis.core.ioc.context.bean;

import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.ioc.context.annotation.BeanScope;

public class PrototypeBeanContext extends AbstractBeanContext {

	public PrototypeBeanContext(Class<?> beanType, int order, ApplicationContext applicationContext) {
		super(beanType, order, BeanScope.PROTOTYPE, applicationContext);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getInstance() {
		T beanInstance = this.instanciateBeanAndResolveDependencies();
		this.callPostConstructMethod(beanInstance);
		return beanInstance;
	}
}
