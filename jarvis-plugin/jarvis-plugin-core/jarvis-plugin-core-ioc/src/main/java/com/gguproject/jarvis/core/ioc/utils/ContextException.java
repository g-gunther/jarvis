package com.gguproject.jarvis.core.ioc.utils;

public class ContextException extends RuntimeException {
	private static final long serialVersionUID = -2124739326687058438L;

	public ContextException(String message) {
		super(message);
	}
	
	public ContextException(String message, Exception e) {
		super(message, e);
	}
	
	public ContextException(Exception e) {
		super(e);
	}
}
