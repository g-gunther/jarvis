package com.gguproject.jarvis.plugin.freeboxv6.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.freeboxv6.service.KodiProcess;

import javax.inject.Named;

@Named
@Qualifier(KodiCommandProcessor.qualifier)
public class KodiNetflixCommandProcessor extends AbstractCommandProcessor{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(KodiNetflixCommandProcessor.class);

	private final KodiProcess kodiProcess;

	public KodiNetflixCommandProcessor(KodiProcess kodiProcess) {
		super("netflix", "Select the netflix plugin");
		this.kodiProcess = kodiProcess;
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		this.kodiProcess.startAndSetNetflix();
		return CommandOutputBuilder.build("Command executed").get();
	}
}
