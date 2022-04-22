package com.gguproject.jarvis.plugin.deepspeech.service;

import javax.inject.Named;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.core.PluginApplicationContextAware;

/**
 * Manager which handles the deepspeech recognition thread
 */
@Named
public class DeepspeechManager {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(DeepspeechManager.class);
	
	private DeepspeechThread deepspeechThread;
	
	/**
	 * Build a new deepspeech recognition thread if doesn't exist yet
	 * If there is already a thread running, it throws an {@link IllegalStateException}
	 */
	public void buildAndStart() {
		if(this.deepspeechThread != null) {
			throw new IllegalStateException("There is an already running deepspeech recognition thread");
		}
		
		LOGGER.debug("Build and start the deepspeech recognition thread");
		this.deepspeechThread = PluginApplicationContextAware.getApplicationContext().getBean(DeepspeechThread.class);
		this.deepspeechThread.start();
	}
	
	/**
	 * Restart the speech recognition thread
	 * by interrupting the existing one a creating a fresh new thread
	 */
	public void restart() {
		LOGGER.debug("Restart speech recognition thread");
		
		this.deepspeechThread.interrupt();
		this.deepspeechThread = null;
		
		this.buildAndStart();
	}
}
