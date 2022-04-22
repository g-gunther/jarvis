package com.gguproject.jarvis.plugin.freeboxv6.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.freeboxv6.service.FreeboxServerService;
import com.gguproject.jarvis.plugin.freeboxv6.service.dto.PlayerStatus;

import javax.inject.Named;

@Named
@Qualifier(FreeboxCommandProcessor.qualifier)
public class FreeboxPlayerStatusCommandProcessor extends AbstractCommandProcessor{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(FreeboxPlayerStatusCommandProcessor.class);

	private final FreeboxServerService freeboxServerService;

	public FreeboxPlayerStatusCommandProcessor(FreeboxServerService freeboxServerService) {
		super("status", "Retrieve the status of the freebox player");
		this.freeboxServerService = freeboxServerService;
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		if(!command.hasArgument("player_id")) {
			return CommandOutputBuilder.build("There is no 'player_id' arguments in command: ''{0}''", command.getCommand()).get();
		}

		PlayerStatus status = freeboxServerService.playerStatus(command.getArgument("player_id"));
		return CommandOutputBuilder.build("Status of player {0}: {1}", command.getArgument("player_id"), status).get();
	}
}
