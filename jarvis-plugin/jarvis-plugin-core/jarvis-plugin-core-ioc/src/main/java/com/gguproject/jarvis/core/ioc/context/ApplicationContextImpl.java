package com.gguproject.jarvis.core.ioc.context;

import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.core.ioc.ApplicationContextManager;
import com.gguproject.jarvis.core.ioc.context.bean.BeanContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the application context
 */
public class ApplicationContextImpl implements ApplicationContext, InternalApplicationContext{

	/**
	 * Parent application context manager 
	 */
	private ApplicationContextManager applicationContextManager;
	
	/**
	 * List of beans handled by this application context
	 */
	private Map<Class<?>, List<BeanContext>> beans = new HashMap<>();

	private class ApplicationContextBeanContext implements BeanContext {

		private final ApplicationContext applicationContext;

		public ApplicationContextBeanContext(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}

		public Class<?> getBeanType(){return ApplicationContext.class;}
		public ApplicationContext getInstance() {
			return this.applicationContext;
		}
	}
	
	public ApplicationContextImpl(ApplicationContextManager applicationContextManager) {
		this.registerBean(new ApplicationContextBeanContext(this));
		this.applicationContextManager = applicationContextManager;
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> beanClass) {
		Optional<T> foundBean = this.getOptionalBean(beanClass);

		if(foundBean.isEmpty()){
			throw TechnicalException.get().message("No instance of {0} found", beanClass).build();
		}
		return foundBean.get();
	}

	public <T> T getBean(Class<T> beanClass, String qualifier) {
		Optional<T> foundBean = this.getOptionalBean(beanClass, qualifier);

		if(foundBean.isEmpty()) {
			throw TechnicalException.get().message("No bean of type {0} with qualifier {1} found", beanClass, qualifier).build();
		}

		return foundBean.get();
	}

	public <T> Optional<T> getOptionalBean(Class<T> beanClass) {
		List<BeanContext> beans = this.getBeanContexts(beanClass);

		if(beans.size() == 1) {
			// if the bean wasn't initialized yet, do it
			T beanInstance = beans.get(0).getInstance();
			if(beanInstance == null) {
				throw TechnicalException.get().message("Bean instance {0} is null - probably because under creation - check circular dependencies ", beanClass).build();
			}
			return Optional.of(beans.get(0).getInstance());
		} else if(beans.size() == 0){
			return Optional.empty();
		} else {
			throw TechnicalException.get().message("Multiple instance of {0}} has been found in the context", beanClass).build();
		}
	}

	public <T> Optional<T> getOptionalBean(Class<T> beanClass, String qualifier) {
		if(qualifier == null) {
			throw TechnicalException.get().message("Can''t get beans of type {0} with null qualifier", beanClass).build();
		}

		List<BeanContext> beans = this.getBeanContexts(beanClass);
		if(beans.size() == 0){
			return Optional.empty();
		}
		List<BeanContext> beansWithQualifier = beans.stream()
				.filter(c -> qualifier.equals(c.getQualifier()))
				.collect(Collectors.toList());

		if(beansWithQualifier.size() > 1) {
			throw TechnicalException.get().message("Several beans of type {0} with qualifier {1} found", beanClass, qualifier).build();
		} else if(beansWithQualifier.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(beansWithQualifier.get(0).getInstance());
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getBeans(Class<T> beanClass){
		List<T> foundBeans = new ArrayList<>();
		if(this.beans.containsKey(beanClass)) {
			foundBeans.addAll((List<T>) (this.beans.get(beanClass).stream()
				.sorted((c1, c2) -> c1.getOrder() > c2.getOrder() ? 1 : -1)
				.map(c -> (T) c.getInstance())
				.collect(Collectors.toList()))
			);
		}
		
		return foundBeans;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getBeans(Class<T> beanClass, String qualifier){
		if(qualifier == null) {
			throw TechnicalException.get().message("Can''t get beans of type {0} with null qualifier", beanClass).build();
		}
		
		List<T> foundBeans = new ArrayList<>();
		if(this.beans.containsKey(beanClass)) {
			foundBeans.addAll((List<T>) (this.beans.get(beanClass).stream()
				.filter(c -> qualifier.equals(c.getQualifier()))
				.sorted((c1, c2) -> c1.getOrder() > c2.getOrder() ? 1 : -1)
				.map(c -> (T) c.getInstance())
				.collect(Collectors.toList()))
			);
		}
		
		return foundBeans;
	}
	
	public <T> T buildBean(Class<T> beanClass) {
		BeanContext context = this.applicationContextManager.buildSingleInstance(beanClass);
		return context.getInstance();
	}
	
	
	private List<BeanContext> getBeanContexts(Class<?> beanType) {
		return this.beans.containsKey(beanType) ? this.beans.get(beanType) : new ArrayList<>();
	}
	
	public void registerBean(BeanContext beanContext) {
		this.registerBean(beanContext.getBeanType(), beanContext);
	}
	
	public void registerBean(Class<?> superClass, BeanContext beanContext) {
		if(!this.beans.containsKey(superClass)) {
			this.beans.put(superClass, new ArrayList<>());
		}
		this.beans.get(superClass).add(beanContext);
	}
	
	public void registerBean(Set<Class<?>> superClass, BeanContext beanContext) {
		for(Class<?> clazz : superClass) {
			this.registerBean(clazz, beanContext);
		}
	}
}
