package com.gguproject.jarvis.plugin.speech.listener;

public class ShellCommandException extends Exception{
	private static final long serialVersionUID = 8513826787733525472L;

	public ShellCommandException(Exception e){
		super(e);
	}
}
