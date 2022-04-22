package com.gguproject.jarvis.helper.sound;

import com.gguproject.jarvis.core.exception.BusinessException;

public class SoundPlayerException extends BusinessException {
	private static final long serialVersionUID = 1639795341781554278L;

	public SoundPlayerException(String message) {
		super(message);
	}
	
	public SoundPlayerException(String message, Throwable t) {
		super(message, t);
	}
}
