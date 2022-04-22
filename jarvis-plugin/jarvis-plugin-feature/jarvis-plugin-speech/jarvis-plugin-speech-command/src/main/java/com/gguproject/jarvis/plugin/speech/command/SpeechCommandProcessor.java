package com.gguproject.jarvis.plugin.speech.command;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.speech.event.SpeechEventData;

import javax.inject.Named;

@Named
@Qualifier(AbstractCommandProcessor.rootQualifier)
public class SpeechCommandProcessor extends AbstractCommandProcessor{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpeechCommandProcessor.class);

	private final EventBusService eventBusService;

	public SpeechCommandProcessor(EventBusService eventBusService) {
		super("speech", "Speech command");
		this.eventBusService = eventBusService;
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		if(!command.hasArgument("text")) {
			return CommandOutputBuilder.build("No text argument found").get();
		}
		
		SpeechEventData event = new SpeechEventData(command.getArgument("text"));
		
		if(command.hasArgument("broadcast") || command.hasArgument("b")) {
			this.eventBusService.externalEmit(event);
			return CommandOutputBuilder.build("Speech has been broadcasted").get();
		} else {
			this.eventBusService.emit(event);
			return CommandOutputBuilder.build("Speech emited internally").get();
		}
	}
}