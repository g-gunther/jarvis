package com.gguproject.jarvis.core.ioc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.ioc.context.ApplicationContextAware;
import com.gguproject.jarvis.core.ioc.context.ApplicationContextImpl;
import com.gguproject.jarvis.core.ioc.context.annotation.BeanScope;
import com.gguproject.jarvis.core.ioc.context.annotation.Prototype;
import com.gguproject.jarvis.core.ioc.context.bean.BeanContext;
import com.gguproject.jarvis.core.ioc.context.builder.BeanContextBuilder;
import com.gguproject.jarvis.core.ioc.context.builder.PrototypeBeanContextBuilder;
import com.gguproject.jarvis.core.ioc.context.builder.SingletonBeanContextBuilder;
import com.gguproject.jarvis.core.ioc.utils.ClassScanner;
import com.gguproject.jarvis.core.ioc.utils.ClassScannerException;
import com.gguproject.jarvis.core.ioc.utils.ContextException;
import com.gguproject.jarvis.core.ioc.utils.ReflectionUtils;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

/**
 * Application context manager
 * Used to scan, register and initialize beans that are annotated by {@link Named}
 */
public class ApplicationContextManager {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(ApplicationContextManager.class);
	
	/**
	 * Instance of the application context
	 */
	private ApplicationContextImpl applicationContext = new ApplicationContextImpl(this);
	
	/**
	 * List of builders used to instantiate beans
	 */
	private final Map<BeanScope, BeanContextBuilder> beanContextBuilder = new HashMap<>();
	
	/**
	 * Current class loader
	 */
	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	
	private ApplicationContextManager() {
		beanContextBuilder.put(BeanScope.SINGLETON, new SingletonBeanContextBuilder(this.applicationContext));
		beanContextBuilder.put(BeanScope.PROTOTYPE, new PrototypeBeanContextBuilder(this.applicationContext));
	}
	
	public static ApplicationContextManager get() {
		return new ApplicationContextManager();
	}
	
	public ApplicationContext getContext() {
		return this.applicationContext;
	}
	
	/**
	 * Start the IoC process
	 * by scanning the given package and registering all classes that are annotated by {@link Named}
	 */
	public ApplicationContextManager scan(String scanPackage) {
		LOGGER.debug("Start scanning on package: {}", scanPackage);
		
		// look for all classes having @Named annotations
		try {
			ClassScanner.get()
				.setPackage(scanPackage)
				.setFilter(c -> c.getAnnotation(Named.class) != null)
				.scanClassLoader()
				.process(classes -> {
					this.registerBeans(classes);
					this.notifyApplicationContextLoaded();
				});
		} catch (ClassScannerException e) {
			throw new ContextException("Can't scan classLoader", e);
		}
		return this;
	}
	
	/**
	 * Register a list of classes in the application context
	 * by finding the {@link BeanContext} associated to the bean scope
	 * @param classes List of classes to register
	 */
	private void registerBeans(Collection<Class<?>> classes) {
		// for each found classes 
		for(Class<?> c : classes) {
			BeanScope beanScope = BeanScope.SINGLETON;
			if(ReflectionUtils.hasAnnotation(Prototype.class, c)) {
				beanScope = BeanScope.PROTOTYPE;
			}
			
			try {
				BeanContext beanContext = this.beanContextBuilder.get(beanScope).buildBeanContext(c);
				
				// register the bean with all super class
				Set<Class<?>> superClass = ReflectionUtils.getAllSuperClass(c);
				applicationContext.registerBean(superClass, beanContext);
			} catch (ContextException e) {
				LOGGER.error("Can't instantiate bean: {}", c, e);
			}
		}
	}
	
	/**
	 * Build a singleton instance of the given class
	 * @param beanClass Class to instantiate
	 * @return Instance of the class
	 */
	public BeanContext buildSingleInstance(Class<?> beanClass) {
		return this.beanContextBuilder.get(BeanScope.SINGLETON).buildBeanContext(beanClass);
	}
	
	/**
	 * Retrieve all classes implementing {@link ApplicationContextAware} and set the application context
	 * once it has been initialized
	 */
	private void notifyApplicationContextLoaded() {
		LOGGER.debug("Notify application context loaded");
		
		this.applicationContext.getBeans(ApplicationContextAware.class).forEach(bean -> {
			bean.setApplicationContext(applicationContext);
		});
	}
	
	/**
	 * Get the class loader of the application context manager
	 * @return
	 */
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}
}
