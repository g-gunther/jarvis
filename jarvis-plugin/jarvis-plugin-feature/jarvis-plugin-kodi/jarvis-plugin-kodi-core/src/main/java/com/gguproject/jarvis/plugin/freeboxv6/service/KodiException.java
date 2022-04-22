package com.gguproject.jarvis.plugin.freeboxv6.service;

import com.gguproject.jarvis.core.exception.BusinessException;

public class KodiException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public KodiException(String message) {
		super(message);
	}
	
	public KodiException(String message, Exception e) {
		super(message, e);
	}
}
