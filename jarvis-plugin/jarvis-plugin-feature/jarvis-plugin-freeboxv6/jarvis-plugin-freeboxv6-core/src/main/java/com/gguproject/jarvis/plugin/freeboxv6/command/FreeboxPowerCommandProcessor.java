package com.gguproject.jarvis.plugin.freeboxv6.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.freeboxv6.service.FreeboxRemoteService;

import javax.inject.Named;

@Named
@Qualifier(FreeboxCommandProcessor.qualifier)
public class FreeboxPowerCommandProcessor extends AbstractCommandProcessor{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(FreeboxPowerCommandProcessor.class);

	private final FreeboxRemoteService freeboxRemoteService;

	public FreeboxPowerCommandProcessor(FreeboxRemoteService freeboxRemoteService) {
		super("power", "Power on/off the freebox play");
		this.freeboxRemoteService = freeboxRemoteService;
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		freeboxRemoteService.power();
		return CommandOutputBuilder.build("Command executed").get();
	}
}
