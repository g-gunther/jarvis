package com.gguproject.jarvis.plugin.spi;

import com.gguproject.jarvis.core.bus.EventBus;
import com.gguproject.jarvis.plugin.spi.data.PluginData;

/**
 * Service used to initialize the plugin (technical point of view)
 */
public interface PluginSystemService {

	/**
	 * Initialize the plugin context
	 * @param data Data related to the loaded plugin
	 */
	public void initialize(PluginData data, EventBus eventBus);
}
