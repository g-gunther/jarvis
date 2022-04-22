package com.gguproject.jarvis.plugin.speech.interpreter;

import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;

/**
 * The idea is to found the action, context, data words to process
 * based on the given grammar
 * @author guillaumegunther
 */
public interface InterpreterService {
	/**
	 * Analyse a given speech to extract contact, action, data, localization and time information
	 * @param speech
	 * @return
	 * @throws InterpreterException
	 */
	public Command interprets(String speech) throws InterpreterException;
	
	/**
	 * Remove any small word that can be found at the beginning of the data
	 * @param data
	 * @return
	 */
	public String cleanDataSpeech(String data);
}
