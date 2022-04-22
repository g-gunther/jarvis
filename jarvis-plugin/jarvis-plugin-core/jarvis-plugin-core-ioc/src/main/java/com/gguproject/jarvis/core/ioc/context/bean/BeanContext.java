package com.gguproject.jarvis.core.ioc.context.bean;

/**
 * Represent a single bean context
 * and is used to instantiate it depending of its scope (Singleton, Prototype...)
 */
public interface BeanContext {

	Class<?> getBeanType();

	/**
	 * Build an instance of the bean depending on the scope
	 * @return Instance of the bean
	 */
	<T> T getInstance();

	default int getOrder() {
		return 0;
	}

	default String getQualifier() {
		return this.getBeanType().getSimpleName();
	}
}
