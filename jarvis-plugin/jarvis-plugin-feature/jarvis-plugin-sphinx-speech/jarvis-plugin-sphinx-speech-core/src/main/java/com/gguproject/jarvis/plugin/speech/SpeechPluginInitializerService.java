package com.gguproject.jarvis.plugin.speech;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.core.PluginLauncher;
import com.gguproject.jarvis.plugin.speech.recognizer.SpeechRecognitionManager;

import javax.inject.Named;

@Named
public class SpeechPluginInitializerService implements PluginLauncher {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpeechPluginInitializerService.class);

	private final SpeechRecognitionManager speechRecognitionManager;

	public SpeechPluginInitializerService(SpeechRecognitionManager speechRecognitionManager){
		this.speechRecognitionManager = speechRecognitionManager;
	}

	public void launch() {
		LOGGER.debug("Initialize the speech plugin");
		speechRecognitionManager.buildAndStart();
	}
}
