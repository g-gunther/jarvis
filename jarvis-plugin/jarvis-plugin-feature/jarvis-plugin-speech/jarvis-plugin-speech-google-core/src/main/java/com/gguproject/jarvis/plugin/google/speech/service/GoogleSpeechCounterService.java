package com.gguproject.jarvis.plugin.google.speech.service;

import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.helper.google.counter.GoogleCounterService;
import com.gguproject.jarvis.plugin.google.speech.SpeechPluginConfiguration;
import com.gguproject.jarvis.plugin.google.speech.SpeechPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.google.speech.service.GoogleSpeechCounterService.CounterName;

import javax.inject.Named;
import java.io.File;

/**
 * Used to check number of request to google speech-to-text service per month
 * @author guillaumegunther
 */
@Named
public class GoogleSpeechCounterService extends GoogleCounterService<CounterName> {

	private final SpeechPluginConfiguration configuration;

	public GoogleSpeechCounterService(SpeechPluginConfiguration configuration){
		this.configuration = configuration;
	}

	@Override
	public File getCounterFile() {
		return this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.googleCounterFileName))
				.orElseThrow(() -> TechnicalException.get().message("Google speech counter file does not exist").build());
	}
	
	/**
	 * List of counter
	 * @author guillaumegunther
	 */
	public enum CounterName {
		SPEECH_REQUEST;
	}
}
