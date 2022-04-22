package com.gguproject.jarvis.core.ioc.utils;

public class ClassScannerException extends Exception {
	private static final long serialVersionUID = -7462710037368655960L;

	public ClassScannerException(String message) {
		super(message);
	}
	
	public ClassScannerException(String message, Exception e) {
		super(message, e);
	}
	
	public ClassScannerException(Exception e) {
		super(e);
	}
}
