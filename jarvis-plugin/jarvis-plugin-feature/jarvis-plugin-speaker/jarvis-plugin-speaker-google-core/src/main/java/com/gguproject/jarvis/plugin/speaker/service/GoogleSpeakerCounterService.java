package com.gguproject.jarvis.plugin.speaker.service;

import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.helper.google.counter.GoogleCounterService;
import com.gguproject.jarvis.plugin.speaker.SpeakerPluginConfiguration;
import com.gguproject.jarvis.plugin.speaker.SpeakerPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.speaker.service.GoogleSpeakerCounterService.CounterName;

import javax.inject.Named;
import java.io.File;

/**
 * Used to check number of request to google speech-to-text service per month
 * @author guillaumegunther
 */
@Named
public class GoogleSpeakerCounterService extends GoogleCounterService<CounterName> {

	private final SpeakerPluginConfiguration configuration;

	public GoogleSpeakerCounterService(SpeakerPluginConfiguration configuration){
		this.configuration = configuration;
	}

	@Override
	public File getCounterFile() {
		return this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.googleCounterFileName))
				.orElseThrow(() -> TechnicalException.get().message("Google speaker counter file does not exist").build());
	}
	
	/**
	 * List of counter
	 * @author guillaumegunther
	 */
	public enum CounterName {
		SPEAKER_REQUEST;
	}
}
