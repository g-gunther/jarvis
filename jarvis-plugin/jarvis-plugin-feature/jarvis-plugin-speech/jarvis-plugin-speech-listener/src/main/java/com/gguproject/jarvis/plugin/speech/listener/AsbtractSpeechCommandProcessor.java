package com.gguproject.jarvis.plugin.speech.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;

/**
 * Classes that extend it are used by the {@link SpeechEventListener}
 * Once a sentence is converted to a command, it tries to find a speech command processor
 * matching the found context and actions to provide it the analyzed command.
 */
public abstract class AsbtractSpeechCommandProcessor {

	/**
	 * Context type
	 */
	private final String context;
	
	/**
	 * List of actions the listener can handle
	 */
	private final List<String> actions = new ArrayList<>();
	
	/**
	 * Constructor
	 * @param contextType Context type
	 * @param actionTypes Actions
	 */
	public AsbtractSpeechCommandProcessor (String context, String...actions){
		this.context = context;
		this.actions.addAll(Arrays.asList(actions));
	}
	
	public boolean canProcess(Command command) {
		return command.getContext().equals(this.context)
			&& this.actions.contains(command.getAction());
	}

	/**
	 * Process the event
	 * @param event Event
	 */
	public abstract void process(DistributedEvent event, Command command);
}
