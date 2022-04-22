package com.gguproject.jarvis.core.exception;

import java.text.MessageFormat;

public class TechnicalExceptionBuilder {

	private String message;
	
	private Exception exception;
	
	public TechnicalExceptionBuilder message(String message, Object...args) {
		this.message = MessageFormat.format(message, args);
		return this;
	}
	
	public TechnicalExceptionBuilder exception(Exception e) {
		this.exception = e;
		return this;
	}
	
	public TechnicalException build() {
		if(this.message != null && this.exception != null) {
			return new TechnicalException(this.message, this.exception);
		} else if(this.message != null) {
			return new TechnicalException(this.message);
		} else if(this.exception != null) {
			return new TechnicalException(this.exception);
		}
		return new TechnicalException();
	}
}
