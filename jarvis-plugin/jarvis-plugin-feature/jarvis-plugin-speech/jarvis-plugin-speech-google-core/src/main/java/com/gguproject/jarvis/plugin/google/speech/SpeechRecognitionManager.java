package com.gguproject.jarvis.plugin.google.speech;

import javax.inject.Named;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.core.PluginApplicationContextAware;

/**
 * Manager which handles the speech recognition thread
 */
@Named
public class SpeechRecognitionManager {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpeechRecognitionManager.class);
	
	private SpeechRecognitionThread speechRecognitionThread;
	
	/**
	 * Build a new speech recognition thread if doesn't exist yet
	 * If there is already a thread running, it throws an {@link IllegalStateException}
	 */
	public void buildAndStart() {
		if(this.speechRecognitionThread != null) {
			throw new IllegalStateException("There is an already running speech recognition thread");
		}
		
		LOGGER.debug("Build and start the speech recognition thread");
		this.speechRecognitionThread = PluginApplicationContextAware.getApplicationContext().getBean(SpeechRecognitionThread.class);
		this.speechRecognitionThread.start();
	}
	
	/**
	 * Restart the speech recognition thread
	 * by interrupting the existing one a creating a fresh new thread
	 */
	public void restart() {
		LOGGER.debug("Restart speech recognition thread");
		
		this.speechRecognitionThread.interrupt();
		this.speechRecognitionThread = null;
		
		this.buildAndStart();
	}
}
