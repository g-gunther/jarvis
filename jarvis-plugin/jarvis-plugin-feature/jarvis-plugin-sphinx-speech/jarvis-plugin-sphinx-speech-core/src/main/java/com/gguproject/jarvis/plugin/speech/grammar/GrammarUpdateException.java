package com.gguproject.jarvis.plugin.speech.grammar;

/**
 * Internal exception
 * used when parsing the speech configuration file 
 */
public class GrammarUpdateException extends Exception {
	private static final long serialVersionUID = -6609331731479650197L;

	public GrammarUpdateException(String message) {
		super(message);
	}
	
	public GrammarUpdateException(String message, Exception e) {
		super(message, e);
	}
}