package com.gguproject.jarvis.core.exception;

import java.text.MessageFormat;

public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 5368770069726837692L;

	public BusinessException() {
		super();
	}
	
	public BusinessException(String message) {
		super(message);
	}
	
	public BusinessException(String message, Object...args) {
		super(MessageFormat.format(message, args));
	}
	
	public BusinessException(Exception e) {
		super(e);
	}
	
	public BusinessException(String message, Exception e) {
		super(message, e);
	}
}