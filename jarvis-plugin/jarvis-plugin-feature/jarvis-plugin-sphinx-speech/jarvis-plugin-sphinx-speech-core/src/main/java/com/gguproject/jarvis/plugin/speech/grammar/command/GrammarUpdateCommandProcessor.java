package com.gguproject.jarvis.plugin.speech.grammar.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.speech.event.GrammarUpdateEventData;
import com.gguproject.jarvis.plugin.speech.event.GrammarUpdateEventData.Action;
import com.gguproject.jarvis.plugin.speech.event.GrammarUpdateEventData.TargetElement;
import com.gguproject.jarvis.plugin.speech.grammar.GrammarUpdateService;

import javax.inject.Named;

/**
 * Command processor to update the gramar
 */
@Named
@Qualifier(GrammarCommandProcessor.qualifier)
public class GrammarUpdateCommandProcessor extends AbstractCommandProcessor {

	private final GrammarUpdateService grammarUpdateService;

	public GrammarUpdateCommandProcessor(GrammarUpdateService grammarUpdateService) {
		super("update", "Restart the speech recognition engine");
		this.grammarUpdateService = grammarUpdateService;
	}

	@Override
	public CommandOutput process(CommandRequest command) {
		if(!command.hasArgument("action")) {
			return CommandOutputBuilder.build("There is no 'action' arguments in command: ''{0}''", command.getCommand()).get();
		}
		if(!command.hasArgument("target")) {
			return CommandOutputBuilder.build("There is no 'target' arguments in command: ''{0}''", command.getCommand()).get();
		}
		
		Action action = Action.valueOf(command.getArgument("action"));
		TargetElement target = TargetElement.valueOf(command.getArgument("target"));
		
		GrammarUpdateEventData updateEvent = new GrammarUpdateEventData(action, target, command.getArgument("entry"), command.getArgument("value"));
		if(this.grammarUpdateService.update(updateEvent)) {
			return CommandOutputBuilder.build("The grammar has been updated").get();
		} else {
			return CommandOutputBuilder.build("An error occurs while updating the grammar").get();
		}
	}
	//gram update -action=ADD -target=ACTION -entry=SET_PLAYLIST -value=met playlist
}