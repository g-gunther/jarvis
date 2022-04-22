package com.gguproject.jarvis.core.ioc.utils;

import com.gguproject.jarvis.core.exception.TechnicalException;

public class ReflectionException extends TechnicalException {
	private static final long serialVersionUID = -7462710037368655960L;

	public ReflectionException(String message) {
		super(message);
	}
	
	public ReflectionException(String message, Exception e) {
		super(message, e);
	}
	
	public ReflectionException(Exception e) {
		super(e);
	}
}
