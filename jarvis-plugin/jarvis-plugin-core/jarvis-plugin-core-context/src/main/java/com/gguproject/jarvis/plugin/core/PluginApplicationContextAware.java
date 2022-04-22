package com.gguproject.jarvis.plugin.core;

import javax.inject.Named;

import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.ioc.context.ApplicationContextAware;

/**
 * Implement the {@link ApplicationContextAware} to be able to retrieve it
 * and know if the plugin has already been loaded or not
 */
@Named
public class PluginApplicationContextAware implements ApplicationContextAware {

	public static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext context) {
		applicationContext = context;
	}

	public static boolean isApplicationContextLoaded() {
		return applicationContext != null;
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
