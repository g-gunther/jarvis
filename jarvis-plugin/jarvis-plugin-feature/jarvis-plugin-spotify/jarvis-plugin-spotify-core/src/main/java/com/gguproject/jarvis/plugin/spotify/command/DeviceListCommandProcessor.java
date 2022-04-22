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
import java.util.stream.Collectors;

@Named
@Qualifier(DeviceCommandProcessor.qualifier)
public class DeviceListCommandProcessor extends AbstractCommandProcessor {

	private final SpotifyService spotifyService;

	private final DeviceConfigurationService deviceConfigurationService;

	public DeviceListCommandProcessor(SpotifyService spotifyService, DeviceConfigurationService deviceConfigurationService) {
		super("list", "list all devices");
		this.spotifyService = spotifyService;
		this.deviceConfigurationService = deviceConfigurationService;
	}

	@Override
	public CommandOutput process(CommandRequest command) {
		List<DeviceDto> devices = this.spotifyService.listDevices();
		DeviceDto selectedDevice = deviceConfigurationService.getDevice();
		if(devices.isEmpty()) {
			return CommandOutputBuilder.build("No devices found").get();
		} else {
			return new CommandOutput(
					devices.stream().map(p -> {
						String output = String.format("- '%s' with id: '%s'", p.getName(), p.getId());
						if(selectedDevice != null && selectedDevice.getId().equals(p.getId())) {
							return output + " (selected)";
						}
						return output;
					})
				.collect(Collectors.toList())
			);
		}
	}
}
