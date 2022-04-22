package com.gguproject.jarvis.plugin.cec.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.cec.service.CecException;
import com.gguproject.jarvis.plugin.cec.service.CecService;

import javax.inject.Named;

@Named
@Qualifier(CecCommandProcessor.qualifier)
public class CecTurnOnCommandProcessor extends AbstractCommandProcessor{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(CecTurnOnCommandProcessor.class);

	private final CecService cecService;

	public CecTurnOnCommandProcessor(CecService cecService) {
		super("on", "Turn the device on");
		this.cecService = cecService;
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		try {
			this.cecService.turnDeviceOn();
			return CommandOutputBuilder.build("Request turn on executed").get();
		} catch (CecException e) {
			LOGGER.error("Error while processing turnon request", e);
			return CommandOutputBuilder.build("Error while processing turn on command: {1}", e.getMessage()).error().get();
		}
	}
}
