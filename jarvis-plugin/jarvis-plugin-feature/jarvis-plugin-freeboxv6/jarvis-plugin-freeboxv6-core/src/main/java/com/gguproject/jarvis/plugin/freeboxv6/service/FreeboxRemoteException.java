package com.gguproject.jarvis.plugin.freeboxv6.service;

import com.gguproject.jarvis.core.exception.BusinessException;

public class FreeboxRemoteException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public FreeboxRemoteException(String message) {
		super(message);
	}
	
	public FreeboxRemoteException(String message, Exception e) {
		super(message, e);
	}
}
