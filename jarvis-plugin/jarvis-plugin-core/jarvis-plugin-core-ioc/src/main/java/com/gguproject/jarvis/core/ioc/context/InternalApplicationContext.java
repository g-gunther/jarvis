package com.gguproject.jarvis.core.ioc.context;

import com.gguproject.jarvis.core.ioc.context.bean.BeanContext;

import java.util.Set;

/**
 * Internal application context
 * Contains framework methods used to initialize it
 */
public interface InternalApplicationContext extends ApplicationContext {
	/**
	 * Register a bean bean context
	 * @param beanContext Bean context to register
	 */
	public void registerBean(BeanContext beanContext);
	
	/**
	 * Register a bean context on a given super class
	 * @param superClass Super class of the bean context
	 * @param beanContext Bean context to register
	 */
	public void registerBean(Class<?> superClass, BeanContext beanContext);
	
	/**
	 * Register a bean on a given list of super class
	 * @param superClass List of super classes
	 * @param beanContext Bean context to register
	 */
	public void registerBean(Set<Class<?>> superClass, BeanContext beanContext);
}
