package com.gguproject.jarvis.helper.shell;

import com.gguproject.jarvis.core.exception.TechnicalException;

public class ShellCommandException extends TechnicalException {
	private static final long serialVersionUID = 8513826787733525472L;

	public ShellCommandException(Exception e){
		super(e);
	}
}
