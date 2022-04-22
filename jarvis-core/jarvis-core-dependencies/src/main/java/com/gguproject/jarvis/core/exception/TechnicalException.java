package com.gguproject.jarvis.core.exception;

public class TechnicalException extends RuntimeException {
	private static final long serialVersionUID = -7544439848286463282L;

	public TechnicalException() {
		super();
	}
	
	public TechnicalException(String message) {
		super(message);
	}
	
	public TechnicalException(Exception e) {
		super(e);
	}
	
	public TechnicalException(String message, Exception e) {
		super(message, e);
	}
	
	public static TechnicalExceptionBuilder get() {
		return new TechnicalExceptionBuilder();
	}
}
