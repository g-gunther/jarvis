package com.gguproject.jarvis.core.ioc.context;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;

import java.util.List;
import java.util.Optional;

/**
 * Application context
 * Used to retrieve and build beans
 */
public interface ApplicationContext {

	/**
	 * Get a bean instance by its class
	 * @param beanClass Class of the bean to find
	 * @return Bean instance
	 */
	public <T> T getBean(Class<T> beanClass);

	/**
	 * Get an optional containing the bean
	 * @param beanClass
	 * @param <T>
	 * @return
	 */
	<T> Optional<T> getOptionalBean(Class<T> beanClass);

	/**
	 * Get an optional containing the bean
	 * @param beanClass
	 * @param <T>
	 * @return
	 */
	<T> Optional<T> getOptionalBean(Class<T> beanClass, String qualifier);

	/**
	 * Get a single bean instance by its class & name
	 * @param beanClass Bean type
	 * @Param qualifier Qualifier value
	 * @param <T>
	 * @return
	 */
	public <T> T getBean(Class<T> beanClass, String qualifier);
	
	/**
	 * Get a list of beans having the given class
	 * @param beanClass Class of beans to find
	 * @return List of instances
	 */
	public <T> List<T> getBeans(Class<T> beanClass);
	
	/**
	 * Get a list of beans having the given class and the given classifer {@link Qualifier}
	 * @param beanClass Class of beans to find
	 * @param qualifier Qualifier value
	 * @return List of instances
	 */
	public <T> List<T> getBeans(Class<T> beanClass, String qualifier);
	
	/**
	 * Build an instance of the given bean class
	 * @param beanClass Bean class 
	 * @return Instance of bean
	 */
	public <T> T buildBean(Class<T> beanClass);
}
