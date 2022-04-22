package com.gguproject.jarvis.plugin.spotify.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.spotify.service.DeviceConfigurationService;
import com.gguproject.jarvis.plugin.spotify.service.SpotifyServiceOrchestrator;

import javax.inject.Named;

/**
 * Select the registered device
 */
@Named
@Qualifier(DeviceCommandProcessor.qualifier)
public class DeviceSelectionCommandProcessor extends AbstractCommandProcessor {

	private final SpotifyServiceOrchestrator spotifyServiceOrchestrator;

	private final DeviceConfigurationService deviceConfigurationService;

	public DeviceSelectionCommandProcessor(SpotifyServiceOrchestrator spotifyServiceOrchestrator, DeviceConfigurationService deviceConfigurationService) {
		super("select", "Select the registered device to play music");
		this.spotifyServiceOrchestrator = spotifyServiceOrchestrator;
		this.deviceConfigurationService = deviceConfigurationService;
	}

	@Override
	public CommandOutput process(CommandRequest command) {
		if(this.deviceConfigurationService.getDevice() == null) {
			return CommandOutputBuilder.build("There is no registered device").get();
		}
		
		this.spotifyServiceOrchestrator.play();
		return CommandOutputBuilder.build("Device: ''{0}'' has been selected", this.deviceConfigurationService.getDevice().getName()).get();
	}
}
