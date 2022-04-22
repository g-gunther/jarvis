package com.gguproject.jarvis.plugin.freeboxv6.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.freeboxv6.service.FreeboxServerService;
import com.gguproject.jarvis.plugin.freeboxv6.service.dto.FreeboxServerListPlayerResponse;

import javax.inject.Named;

@Named
@Qualifier(FreeboxCommandProcessor.qualifier)
public class FreeboxListPlayersCommandProcessor extends AbstractCommandProcessor{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(FreeboxListPlayersCommandProcessor.class);

	private final FreeboxServerService freeboxServerService;

	public FreeboxListPlayersCommandProcessor(FreeboxServerService freeboxServerService) {
		super("list", "List all the freebox players");
		this.freeboxServerService = freeboxServerService;
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		FreeboxServerListPlayerResponse players = freeboxServerService.listPlayers();
		CommandOutputBuilder builder = CommandOutputBuilder.build("List of freebox players");
		players.forEach(player -> {
			builder.newLine()
					.append("id={0}, name={1}, api available={2}, reachable={3}, api version={4}", player.getId(), player.getDeviceName(), player.isApiAvailable(), player.isReachable(), player.getApiVersion());
		});
		return builder.get();
	}
}
