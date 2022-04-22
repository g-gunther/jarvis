package com.gguproject.jarvis.plugin.core;

import javax.inject.Named;

/**
 * Interface plugins has to implements to initialize it
 * Implementation will be injected (so they have to be annotated with {@link Named}
 */
public interface PluginLauncher {

	/**
	 * Initialize the plugins
	 */
	public void launch();
}
