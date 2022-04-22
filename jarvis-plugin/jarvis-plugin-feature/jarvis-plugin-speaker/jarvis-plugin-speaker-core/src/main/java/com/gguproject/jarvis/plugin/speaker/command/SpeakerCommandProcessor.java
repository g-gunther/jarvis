package com.gguproject.jarvis.plugin.speaker.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.speaker.SpeakerService;

import javax.inject.Named;

@Named
@Qualifier(AbstractCommandProcessor.rootQualifier)
public class SpeakerCommandProcessor extends AbstractCommandProcessor{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpeakerCommandProcessor.class);

	private final SpeakerService speakerService;

	public SpeakerCommandProcessor(SpeakerService speakerService) {
		super("speak", "Speak command");
		this.speakerService = speakerService;
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		if(!command.hasArgument("text") && !command.hasArgument("code")) {
			return CommandOutputBuilder.build("No text argument found").get();
		}

		if(command.hasArgument("text")){
			this.speakerService.speak(command.getArgument("text"));
			return CommandOutputBuilder.build("Play speech: {0}", command.getArgument("text")).get();
		} else {
			this.speakerService.speakByCode(command.getArgument("code"));
			return CommandOutputBuilder.build("Play speech code: {0}", command.getArgument("code")).get();
		}
	}
}