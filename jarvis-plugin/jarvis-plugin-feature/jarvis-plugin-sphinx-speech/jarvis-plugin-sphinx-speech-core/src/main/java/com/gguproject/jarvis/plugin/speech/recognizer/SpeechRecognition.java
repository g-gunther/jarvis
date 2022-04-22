package com.gguproject.jarvis.plugin.speech.recognizer;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.core.ioc.context.annotation.Prototype;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration.PropertyKey;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.api.SpeechResult;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Speech recognition system based on sphinx 4
 */
@Named
@Prototype
public class SpeechRecognition implements OnPostConstruct {
	/** Log */
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpeechRecognition.class);
	
	/**
	 * Sphinx 4 speech recognizer
	 */
	private JarvisSpeechRecognizer speechRecognizer;
	
	/**
	 * Unknow recognize value
	 */
	private final static String sphinxUnknowRecognition = "<unk>";

	private final SpeechPluginConfiguration configuration;
	
	private boolean interrupted = false;

	public SpeechRecognition(SpeechPluginConfiguration configuration){
		this.configuration = configuration;
	}
	
	/**
	 * Setup the configuration of sphinx
	 * @throws IOException An error occurs during the load of the context
	 */
	@Override
	public void postConstruct() {
		LOGGER.debug("Setup the speech recognition system");
		
		// force java logging to Warning else sphinx will log a lot of debug stuff...
		java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
		
		Configuration configuration = new Configuration();
		configuration.setAcousticModelPath(this.configuration.getDataFilePath(this.configuration.getProperty(PropertyKey.sphinxAcousticModelPath)));
		configuration.setDictionaryPath(this.configuration.getDataFilePath(this.configuration.getProperty(PropertyKey.sphinxDictionaryPath)));
		
		File grammarPath = this.configuration.getConfigurationDirectory(this.configuration.getProperty(PropertyKey.sphinxGrammarPath))
				.orElseThrow(() -> TechnicalException.get().message("The configured grammar path does not exists: {0}", this.configuration.getProperty(PropertyKey.sphinxGrammarPath)).build());
		LOGGER.info("Grammar path: {}", grammarPath.getPath());
		configuration.setGrammarPath(grammarPath.getPath());

		
		configuration.setGrammarName(this.configuration.getProperty(PropertyKey.sphinxGrammarName));
		configuration.setUseGrammar(true);
		
		try {
			this.speechRecognizer = new JarvisSpeechRecognizer(new Context(this.configuration.getDataFilePath(this.configuration.getProperty(PropertyKey.sphinxConfiguration)), configuration));
		} catch (IOException e) {
			LOGGER.error("Not able to start the speech recognizer", e);
		}
	}

	public String waitForNextSpeech() throws InterruptedException {
		LOGGER.debug("Start speech recognizer");
		this.speechRecognizer.startRecognition(true);
		
		SpeechResult speechResult;
		String speech;
		while(true){
			// this operation can't be stopped. Need to wait until it's done 
			// before interrupting the recognition thread
			speechResult = this.speechRecognizer.getResult();
			
			// If the interruption has been request, leave the loop
			if(this.interrupted) {
				throw new InterruptedException("Speech recognition has been interrupted");
			}
			
			speech = speechResult.getHypothesis();

			// found a speech which is not unknown
			if(StringUtils.isNotEmpty(speech) && !sphinxUnknowRecognition.equals(speech)){
				break;
			}
		}
		
		this.speechRecognizer.stopRecognition();
		
		LOGGER.debug("Found speech: {}", speech);
		return speech;
	}
	
	/**
	 * Interrupt the speech recognition
	 */
	public void interrupt() {
		LOGGER.debug("Interrupt SpeechRecognition");
		this.interrupted = true;
	}
}
