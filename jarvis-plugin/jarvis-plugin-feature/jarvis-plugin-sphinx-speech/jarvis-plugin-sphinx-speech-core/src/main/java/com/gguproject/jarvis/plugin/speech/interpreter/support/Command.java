package com.gguproject.jarvis.plugin.speech.interpreter.support;

/**
 * Represents a found command
 */
public class Command {

	/**
	 * Command context
	 */
	private String context;
	
	/**
	 * Command action
	 */
	private String action;
	
	/**
	 * Command data
	 */
	private String data;
	
	/**
	 * Command localization
	 */
	private String localization;
	
	public Command() {}
	
	public Command(String context, String action) {
		this.action = action;
		this.context = context;
	}
	
	public Command(String context, String action, String data) {
		this(context, action);
		this.data = data;
	}
	
	public Command(String context, String action, String data, String localization) {
		this(context, action, data);
		this.localization = localization;
	}

	public String getContext() {
		return context;
	}

	public String getAction() {
		return action;
	}

	public String getData() {
		return data;
	}
	
	public String getLocalisation() {
		return this.localization;
	}

	@Override
	public String toString() {
		return "Command [context=" + context + ", action=" + action + ", data=" + data + ", localization="
				+ localization + "]";
	}
}
