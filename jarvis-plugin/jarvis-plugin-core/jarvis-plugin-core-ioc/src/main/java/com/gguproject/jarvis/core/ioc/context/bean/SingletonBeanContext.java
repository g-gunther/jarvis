package com.gguproject.jarvis.core.ioc.context.bean;

import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.ioc.context.annotation.BeanScope;

public class SingletonBeanContext extends AbstractBeanContext {

	private Object instance;
	
	public SingletonBeanContext(Class<?> beanType, int order, ApplicationContext applicationContext) {
		super(beanType, order, BeanScope.SINGLETON, applicationContext);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getInstance() {
		if(!this.initialized) {
			this.initialized = true;
			this.instance = this.instanciateBeanAndResolveDependencies();
			this.callPostConstructMethod(this.instance);
		}
		return (T) this.instance;
	}

}
