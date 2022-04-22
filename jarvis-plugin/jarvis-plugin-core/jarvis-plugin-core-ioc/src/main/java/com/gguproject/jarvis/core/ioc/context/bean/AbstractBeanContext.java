package com.gguproject.jarvis.core.ioc.context.bean;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.ioc.context.annotation.BeanScope;
import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.ioc.context.bean.beanresolver.*;
import com.gguproject.jarvis.core.ioc.utils.ContextException;
import com.gguproject.jarvis.core.ioc.utils.ReflectionUtils;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represent a single bean context
 * and is used to instantiate it depending of its scope (Singleton, Prototype...)
 */
public abstract class AbstractBeanContext implements BeanContext {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(AbstractBeanContext.class);

	private static final Object[] emptyArgs = new Object[] {};

	/**
	 * Default value injector - used to inject a single object property annotated by {@link Inject}
	 */
	private BeanResolver defaultFieldInjector = new SingleBeanResolver();

	/**
	 * List of custom value injectors (list, arraylist ...)
	 */
	private static List<BeanResolver> fieldInjectors = List.of(
			new ListBeanResoler(),
			new OptionalBeanResolver()
	);

	/**
	 * Class of the bean
	 */
	private Class<?> beanType;

	/**
	 * bean scope (Singleton, prototype...)
	 */
	private BeanScope beanScope;

	/**
	 * Order
	 */
	private int order = 0;

	/**
	 * Parent application context
	 */
	private ApplicationContext applicationContext;

	/**
	 * Qualifier value if any {@link Qualifier}
	 */
	private String qualifier;

	/**
	 * Indicate if the bean has already been initialized
	 */
	protected boolean initialized = false;

	/**
	 * Constructor
	 * @param beanType Class of the bean
	 * @param beanScope Bean scope
	 * @param applicationContext Parent application context
	 */
	public AbstractBeanContext(Class<?> beanType, int order, BeanScope beanScope, ApplicationContext applicationContext) {
		this.beanType = beanType;
		this.order = order;
		this.applicationContext = applicationContext;
		this.beanScope = beanScope;

		// Set the qualifier if the annotation is set
		if(ReflectionUtils.hasAnnotation(Qualifier.class, beanType)) {
			Qualifier qualifierAnnotation = ReflectionUtils.getAnnotation(Qualifier.class, beanType);
			this.qualifier = qualifierAnnotation.value();
		}
	}

	protected <T> T instanciateBeanAndResolveDependencies() {
		Constructor beanConstructor;
		if(this.beanType.getDeclaredConstructors().length == 1){
			beanConstructor = this.beanType.getDeclaredConstructors()[0];
		} else {
			List<Constructor> constructorsWithInject = Stream.of(this.beanType.getDeclaredConstructors())
				.filter(constructor -> constructor.isAnnotationPresent(Inject.class))
				.collect(Collectors.toList());

			if(constructorsWithInject.size() > 1) {
				throw new IllegalStateException("Several constructor with @Inject found for bean: " + this.beanType);
			}

			if(constructorsWithInject.size() == 1){
				beanConstructor = constructorsWithInject.get(0);
			} else {
				beanConstructor = Stream.of(this.beanType.getDeclaredConstructors())
						.filter(constructor -> constructor.getParameterCount() == 0)
						.findFirst()
						.orElseThrow(() -> new IllegalStateException("no default constructor found for bean: " + this.beanType));
			}
		}

		try {
			List<Object> constructorParameters = new ArrayList<>();
			List<BeanResolverField> fields = BeanResolverField.from(beanConstructor.getParameterAnnotations(), beanConstructor.getGenericParameterTypes());
			constructorParameters.addAll(
					fields.stream()
							.map(field -> fieldInjectors.stream()
									.filter(injector -> injector.accept(field))
									.findFirst()
									.orElse(defaultFieldInjector)
									.resolve(applicationContext, field)
							).collect(Collectors.toList())
			);

			return (T) beanConstructor.newInstance(constructorParameters.toArray());
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new ContextException(e);
		}
	}

	/**
	 * Once the bean is instantiate, if the bean is an instance of {@link OnPostConstruct} then call the postContruct method
	 * @param instance Bean instance to process
	 */
	protected void callPostConstructMethod(Object instance) {
		try {
			if(instance instanceof OnPostConstruct) {
				((OnPostConstruct) instance).postConstruct();
			}
		} catch (Exception e) {
			throw new ContextException(e);
		}
	}
	
	/**
	 * Return the class of bean
	 * @return Class of the bean
	 */
	public Class<?> getBeanType() {
		return beanType;
	}
	
	/**
	 * Get the bean scope
	 * @return Bean scope
	 */
	public BeanScope getBeanScope() {
		return this.beanScope;
	}
	
	/**
	 * Indicates if the bean is already initialized or not
	 * @return True if initialized
	 */
	public boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * Get the qualifier value
	 * @return Qualifier value
	 */
	public String getQualifier() {
		return this.qualifier;
	}
	
	/**
	 * Get the order value
	 * @return
	 */
	public int getOrder() {
		return this.order;
	}
}
