package com.gguproject.jarvis.plugin.freeboxv6.service;

import com.gguproject.jarvis.core.exception.TechnicalException;

public class HttpRequestException extends TechnicalException {
	private static final long serialVersionUID = 1L;

	public HttpRequestException(String message) {
		super(message);
	}

	public HttpRequestException(String message, Exception e) {
		super(message, e);
	}
}
