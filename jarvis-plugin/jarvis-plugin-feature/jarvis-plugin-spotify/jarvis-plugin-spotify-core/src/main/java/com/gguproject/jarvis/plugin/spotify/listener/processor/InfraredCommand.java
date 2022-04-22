package com.gguproject.jarvis.plugin.spotify.listener.processor;

import java.util.List;
import java.util.Map;

public class InfraredCommand {

	private List<String> actions;
	
	private List<Command> commands;
	
	public List<String> getActions(){
		return this.actions;
	}
	
	public List<Command> getCommands(){
		return this.commands;
	}
	
	@Override
	public String toString() {
		return "InfraredCommand [actions=" + actions + ", commands=" + commands + "]";
	}

	public class Command {
		
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

		@Override
		public String toString() {
			return "Command [context=" + context + ", action=" + action + ", properties=" + properties + "]";
		}
	}
}
