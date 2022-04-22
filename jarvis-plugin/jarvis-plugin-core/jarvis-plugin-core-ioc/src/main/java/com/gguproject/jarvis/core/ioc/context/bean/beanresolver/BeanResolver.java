package com.gguproject.jarvis.core.ioc.context.bean.beanresolver;

import com.gguproject.jarvis.core.ioc.context.ApplicationContext;

public interface BeanResolver {

	boolean accept(BeanResolverField field);

	Object resolve(ApplicationContext applicationContext, BeanResolverField field);
}
