package com.gguproject.jarvis.module.core.command.listener.event;

import com.gguproject.jarvis.core.bus.support.EventData;

/**
 * Command event
 * It contains a command to execute
 */
public class CommandProcessorEventData extends EventData {
	private static final long serialVersionUID = 1503886164768359584L;
	public static final String eventType = "COMMAND_PROCESSOR";
	
	/**
	 * Command
	 */
	private String command;
	
	public CommandProcessorEventData() {
		super(eventType, CommandProcessorEventData.class);
	}
	
	public CommandProcessorEventData(String command) {
		this();
		this.command = command;
	}

	public String getCommand() {
		return this.command;
	}
}
