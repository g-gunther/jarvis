package com.gguproject.jarvis.plugin.speech.grammar;

/**
 * Internal exception
 * used when parsing the speech configuration file 
 */
public class GrammarConfigurationParseException extends Exception {
	private static final long serialVersionUID = -6609331731479650197L;

	public GrammarConfigurationParseException(String message) {
		super(message);
	}
	
	public GrammarConfigurationParseException(String message, Exception e) {
		super(message, e);
	}
}