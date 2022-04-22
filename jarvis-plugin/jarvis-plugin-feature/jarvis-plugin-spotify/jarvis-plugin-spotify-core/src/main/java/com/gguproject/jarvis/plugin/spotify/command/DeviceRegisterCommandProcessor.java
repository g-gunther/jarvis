package com.gguproject.jarvis.plugin.spotify.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.spotify.service.DeviceConfigurationService;
import com.gguproject.jarvis.plugin.spotify.service.SpotifyService;
import com.gguproject.jarvis.plugin.spotify.service.dto.DeviceDto;

import javax.inject.Named;
import java.util.List;
import java.util.Optional;

/**
 * Add a playlist by its id to the speech recognizer
 */
@Named
@Qualifier(DeviceCommandProcessor.qualifier)
public class DeviceRegisterCommandProcessor extends AbstractCommandProcessor {

	private final SpotifyService spotifyService;

	private final DeviceConfigurationService deviceConfigurationService;

	public DeviceRegisterCommandProcessor(SpotifyService spotifyService, DeviceConfigurationService deviceConfigurationService) {
		super("set", "Set the device on which to play music");
		this.spotifyService = spotifyService;
		this.deviceConfigurationService = deviceConfigurationService;
	}

	@Override
	public CommandOutput process(CommandRequest command) {
		if(!command.hasArgument("id")) {
			return CommandOutputBuilder.build("There is no 'id' arguments in command: ''{0}''", command.getCommand()).get();
		}
		
		List<DeviceDto> devices = this.spotifyService.listDevices();
		if(devices.isEmpty()) {
			return CommandOutputBuilder.build("No devices found").get();
		} else {
			Optional<DeviceDto> device = devices.stream().filter(p -> p.getId().equals(command.getArgument("id"))).findFirst();
			if(device.isPresent()) {
				// save the device locally
				this.deviceConfigurationService.setDevice(device.get());
				return CommandOutputBuilder.build("Device with id: ''{0}'' and name: ''{1}'' has been set", command.getArgument("id"), device.get().getName()).get();
			} else {
				return CommandOutputBuilder.build("No devices found for id: ''{0}''", command.getArgument("id")).get();
			}
		}
	}
}
