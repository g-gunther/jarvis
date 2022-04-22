package com.gguproject.jarvis.plugin.speaker;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speaker.SpeakerPluginConfiguration.PropertyKey;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.util.data.audio.AudioPlayer;

import javax.inject.Named;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Named
public class SpeakerService implements OnPostConstruct {
	/** Log */
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpeakerService.class);

	private final SpeakerPluginConfiguration configuration;
	
	/** Marytts main object */
	private MaryInterface marytts;
	
	/** list of properties containing sentences depending on the locale */
	private Map<Locale, Properties> sentences = new ConcurrentHashMap<>();
	
	/** Save the current locale value */
	private volatile Locale currentLocale;

	public SpeakerService(SpeakerPluginConfiguration configuration){
		this.configuration = configuration;
	}

	@Override
	public void postConstruct(){
		// Initialize the marytts main object and select the default language
		try {
			this.marytts = new LocalMaryInterface();
			this.selectVoice(Locale.FRENCH);
		} catch (MaryConfigurationException e) {
			throw TechnicalException.get().message("Not able to initialize marytts interface").exception(e).build();
		}

	}
	
	public void selectVoice(Locale locale){
		if(Locale.ENGLISH.equals(locale)){
			this.marytts.setVoice("cmu-slt-hsmm");
			this.loadProperties(Locale.ENGLISH);
			this.currentLocale = locale;
		} else {
			// Default french voice
			this.marytts.setVoice(configuration.getProperty(PropertyKey.voice));
			this.loadProperties(Locale.FRENCH);
			this.currentLocale = Locale.FRENCH;
		}
	}
	
	/**
	 * Load the speaker file depending on the locale
	 * This file contains all sentences to play
	 * @param locale
	 */
	private void loadProperties(Locale locale){
		if(!this.sentences.containsKey(locale)){
			LOGGER.debug("Load sentence file for locale {}", locale);
			try {
				File file = new File(getClass().getClassLoader().getResource("speaker/speaker-" + locale.getLanguage() + ".properties").getFile());
				InputStream input = new FileInputStream(file);
				Properties prop = new Properties();
				prop.load(input);
				this.sentences.put(locale, prop);
			} catch(IOException e){
				LOGGER.error("Not able to load the speacker file for locale {}", locale.getLanguage());
			}
		}
	}
	
	
	/**
	 * 
	 * @param speechCode
	 */
	public void speakByCode(String speechCode) {
		this.speak(this.getSentence(speechCode));
	}

	/**
	 * 
	 */
	public void speak(String speech){
		try {
			// Generate the audio stream, play it and wait until it ends
			AudioInputStream audio = marytts.generateAudio(speech);
			AudioPlayer player = new AudioPlayer(audio);
			player.start();
			player.join();
		} catch (Exception e) {
			LOGGER.error("Not able to play speech: {}", speech);
		}
	}
	
	/**
	 * Return the sentence if it exists in the property files
	 * else it returns the text directly
	 * @param text Label of text to speech
	 * @return Text to process
	 */
	private String getSentence(String text) {
		String sentence = this.sentences.get(this.currentLocale).getProperty(text);
		if(sentence == null) {
			return text;
		}
		return sentence;
	}
}
