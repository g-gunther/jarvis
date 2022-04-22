package com.gguproject.jarvis.plugin.deepspeech;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.core.PluginLauncher;
import com.gguproject.jarvis.plugin.deepspeech.service.DeepspeechManager;

import javax.inject.Named;

@Named
public class DeepspeechPluginInitializerService implements PluginLauncher {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(DeepspeechPluginInitializerService.class);

	private final DeepspeechManager deepspeechManager;

	public DeepspeechPluginInitializerService(DeepspeechManager deepspeechManager){
		this.deepspeechManager = deepspeechManager;
	}

	public void launch() {
		LOGGER.debug("Initialize the deepspeech plugin");
		deepspeechManager.buildAndStart();
	}
}
