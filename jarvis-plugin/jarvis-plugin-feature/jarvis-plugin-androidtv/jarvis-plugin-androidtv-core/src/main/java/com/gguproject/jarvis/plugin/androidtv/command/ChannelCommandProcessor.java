package com.gguproject.jarvis.plugin.androidtv.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.androidtv.service.AndroidTvRemoteControlService;
import com.gguproject.jarvis.plugin.androidtv.util.AndroidTvException;

import javax.inject.Named;

/**
 * Change the channel of tv
 */
@Named
@Qualifier(TvCommandProcessor.qualifier)
public class ChannelCommandProcessor extends AbstractCommandProcessor {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(ChannelCommandProcessor.class);

	private final AndroidTvRemoteControlService androidTvRemoteControlService;

	public ChannelCommandProcessor(AndroidTvRemoteControlService androidTvRemoteControlService) {
		super("channel", "Change the channel");
		this.androidTvRemoteControlService = androidTvRemoteControlService;
	}

	@Override
	public CommandOutput process(CommandRequest command) {
		if(!command.hasArgument("channel")) {
			return CommandOutputBuilder.build("There is no 'channel' arguments in command: ''{0}''", command.getCommand()).get();
		}
		
		Integer channel = Integer.valueOf(command.getArgument("channel"));
		
		try {
			this.androidTvRemoteControlService.sendChannel(channel);
			return CommandOutputBuilder.build("Channel {0} has been set", channel).get();
		} catch (AndroidTvException e) {
			LOGGER.error("An error occurs while changing the tv channel", e);
			return CommandOutputBuilder.build("An error occurs while changing tv channel: {0}", e.getMessage()).get();
		}
	}
}
