package com.gguproject.jarvis.plugin.speech.interpreter.support;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a found command
 */
public class Command {
	
	public static CommandBuilder builder(String context, String action) {
		return new CommandBuilder(context, action);
	}

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
	private List<String> localizations;
	
	/**
	 * 
	 */
	private Time time; 
	
	public Command() {}
	
	public Command(String context, String action, String data, List<String> localizations, Time time) {
		this.context = context;
		this.action = action;
		this.data = data;
		this.localizations = localizations;
		this.time = time;
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
	
	public List<String> getLocalizations() {
		return this.localizations;
	}
	
	public Time getTime() {
		return this.time;
	}

	@Override
	public String toString() {
		return "Command [context=" + context + ", action=" + action + ", data=" + data + ", localizations="
				+ localizations + ", time=" + time + "]";
	}

	public static class CommandBuilder {
		private String context;
		private String action;
		private String data;
		private List<String> localizations = new ArrayList<>();
		private Time time = Time.build();
		
		public CommandBuilder(String context, String action) {
			this.context = context;
			this.action = action;
		}
		
		public CommandBuilder data(String data) {
			this.data = data;
			return this;
		}
		
		public CommandBuilder localization(String localization) {
			this.localizations.add(localization);
			return this;
		}
		
		public CommandBuilder localizations(List<String> localizations) {
			this.localizations.addAll(localizations);
			return this;
		}
		
		public CommandBuilder time(Time time) {
			this.time = time;
			return this;
		}
		
		public Command build() {
			return new Command(this.context, this.action, this.data, this.localizations, this.time);
		}
	}
}
