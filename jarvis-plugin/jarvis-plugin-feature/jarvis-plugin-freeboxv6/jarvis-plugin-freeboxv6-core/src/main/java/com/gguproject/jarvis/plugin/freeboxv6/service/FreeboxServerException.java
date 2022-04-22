package com.gguproject.jarvis.plugin.freeboxv6.service;

import com.gguproject.jarvis.core.exception.BusinessException;

public class FreeboxServerException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public FreeboxServerException(String message) {
		super(message);
	}

	public FreeboxServerException(String message, Exception e) {
		super(message, e);
	}
}
