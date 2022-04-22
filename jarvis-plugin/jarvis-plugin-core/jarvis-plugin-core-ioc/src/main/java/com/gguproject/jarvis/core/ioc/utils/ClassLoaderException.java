package com.gguproject.jarvis.core.ioc.utils;

import com.gguproject.jarvis.core.exception.TechnicalException;

public class ClassLoaderException extends TechnicalException {
	private static final long serialVersionUID = -7462710037368655960L;

	public ClassLoaderException(String message) {
		super(message);
	}
	
	public ClassLoaderException(String message, Exception e) {
		super(message, e);
	}
	
	public ClassLoaderException(Exception e) {
		super(e);
	}
}
