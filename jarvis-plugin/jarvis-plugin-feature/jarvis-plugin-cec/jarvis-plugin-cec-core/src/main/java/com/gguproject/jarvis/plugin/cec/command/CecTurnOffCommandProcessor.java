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
public class CecTurnOffCommandProcessor extends AbstractCommandProcessor{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(CecTurnOffCommandProcessor.class);

	private final CecService cecService;

	public CecTurnOffCommandProcessor(CecService cecService) {
		super("off", "Turn the device off");
		this.cecService= cecService;
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		try {
			this.cecService.turnDeviceOff();
			return CommandOutputBuilder.build("Request turn off executed").get();
		} catch (CecException e) {
			LOGGER.error("Error while processing turnon request", e);
			return CommandOutputBuilder.build("Error while processing turn off command: {1}", e.getMessage()).error().get();
		}
	}
}
