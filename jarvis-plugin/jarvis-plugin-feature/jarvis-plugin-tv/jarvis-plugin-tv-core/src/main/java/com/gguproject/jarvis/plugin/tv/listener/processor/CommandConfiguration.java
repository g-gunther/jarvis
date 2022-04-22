package com.gguproject.jarvis.plugin.tv.listener.processor;

import java.util.List;
import java.util.Map;

public class CommandConfiguration {

	private List<String> actions;
	
	private List<Command> commands;
	
	public List<String> getActions(){
		return this.actions;
	}
	
	public List<Command> getCommands(){
		return this.commands;
	}

	/**
	 *
	 */
	public class Command {
		private CommandType type;

		private List<Event> events;

		public CommandType getType(){
			return this.type;
		}

		public List<Event> getEvents(){
			return this.events;
		}
	}

	/**
	 *
	 */
	public enum CommandType {
		INFRARED,
		FREEBOXV6;
	}

	/**
	 *
	 */
	public class Event {
		
		private String context;
		
		private String action;
		
		private Map<String, String> properties;
		
		public String getContext() {
			return this.context;
		}
		
		public String getAction() {
			return this.action;
		}
		
		public Map<String, String> getProperties(){
			return this.properties;
		}
	}
}
