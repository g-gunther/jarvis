package com.gguproject.jarvis.plugin.androidtv.util;

public class AndroidTvException extends Exception {
	private static final long serialVersionUID = 7219143062566746258L;

	public AndroidTvException(String message) {
		super(message);
	}
	
	public AndroidTvException(Exception e) {
		super(e);
	}
	
	public AndroidTvException(String message, Exception e) {
		super(message, e);
	}
}
