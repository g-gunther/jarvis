package com.gguproject.jarvis.plugin.speech.interpreter;

import com.gguproject.jarvis.core.exception.BusinessException;

public class InterpreterException extends BusinessException {
	private static final long serialVersionUID = -8380189351947342268L;

	public InterpreterException(String message, Object...args) {
		super(message, args);
	}
}
