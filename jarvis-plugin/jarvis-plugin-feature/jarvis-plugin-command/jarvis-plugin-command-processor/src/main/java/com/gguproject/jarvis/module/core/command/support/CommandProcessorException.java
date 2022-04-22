package com.gguproject.jarvis.module.core.command.support;

import com.gguproject.jarvis.core.exception.BusinessException;

import java.text.MessageFormat;

public class CommandProcessorException extends BusinessException {
	private static final long serialVersionUID = -4287609997286306077L;

	public CommandProcessorException() {
		super();
	}
	
	public CommandProcessorException(String message) {
		super(message);
	}
	
	public CommandProcessorException(String message, Object...args) {
		super(MessageFormat.format(message, args));
	}
	
	public CommandProcessorException(Exception e) {
		super(e);
	}
	
	public CommandProcessorException(String message, Exception e) {
		super(message, e);
	}
}
