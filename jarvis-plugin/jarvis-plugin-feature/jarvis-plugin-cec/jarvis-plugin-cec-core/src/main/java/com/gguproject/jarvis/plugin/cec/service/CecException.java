package com.gguproject.jarvis.plugin.cec.service;

import com.gguproject.jarvis.core.exception.BusinessException;

public class CecException extends BusinessException {
	private static final long serialVersionUID = 643558021003329201L;

	public CecException(String message) {
		super(message);
	}
	
	public CecException(String message, Throwable e) {
		super(message, e);
	}
}
