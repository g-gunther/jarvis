package com.gguproject.jarvis.plugin.cec.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.cec.event.PowerStatus;
import com.gguproject.jarvis.plugin.cec.service.CecException;
import com.gguproject.jarvis.plugin.cec.service.CecService;

import javax.inject.Named;

@Named
@Qualifier(CecCommandProcessor.qualifier)
public class CecStatusCommandProcessor extends AbstractCommandProcessor{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(CecStatusCommandProcessor.class);

	private final CecService cecService;

	public CecStatusCommandProcessor(CecService cecService) {
		super("status", "Get the power and source status");
		this.cecService = cecService;
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		try {
			PowerStatus powerStatus = this.cecService.getPowerStatus();
			boolean activeSource = this.cecService.isActiveSource();
			return CommandOutputBuilder.build("Power status found: {0} - active source: {1}", powerStatus, activeSource).get();
		} catch (CecException e) {
			LOGGER.error("Error while processing status request", e);
			return CommandOutputBuilder.build("Error while processing status command: {0}", e.getMessage()).error().get();
		}
	}
}
