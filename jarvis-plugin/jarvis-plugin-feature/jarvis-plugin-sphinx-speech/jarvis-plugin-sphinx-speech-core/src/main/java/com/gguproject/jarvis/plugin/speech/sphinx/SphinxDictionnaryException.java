package com.gguproject.jarvis.plugin.speech.sphinx;

/**
 * Internal exception
 * used when parsing the sphinc dictionnary file
 */
public class SphinxDictionnaryException extends Exception {
	private static final long serialVersionUID = -6609331731479650197L;

	public SphinxDictionnaryException(String message) {
		super(message);
	}
	
	public SphinxDictionnaryException(String message, Exception e) {
		super(message, e);
	}
}
