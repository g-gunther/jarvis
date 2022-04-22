package com.gguproject.jarvis.plugin.spotify.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.spotify.service.DeviceConfigurationService;
import com.gguproject.jarvis.plugin.spotify.service.SpotifyService;
import com.gguproject.jarvis.plugin.spotify.service.SpotifyServiceOrchestrator;
import com.gguproject.jarvis.plugin.spotify.service.dto.PlaylistDto;

import javax.inject.Named;
import java.util.List;
import java.util.Optional;

/**
 * start playing a given playlist on the registered device
 */
@Named
@Qualifier(PlaylistCommandProcessor.qualifier)
public class PlaylistStartCommandProcessor extends AbstractCommandProcessor {

	private final SpotifyServiceOrchestrator spotifyServiceOrchestrator;

	private final SpotifyService spotifyService;

	private final DeviceConfigurationService deviceConfigurationService;

	public PlaylistStartCommandProcessor(SpotifyServiceOrchestrator spotifyServiceOrchestrator, SpotifyService spotifyService,
										 DeviceConfigurationService deviceConfigurationService) {
		super("start", "Start a playlist on the current selected device");
		this.spotifyServiceOrchestrator = spotifyServiceOrchestrator;
		this.spotifyService = spotifyService;
		this.deviceConfigurationService = deviceConfigurationService;
	}

	@Override
	public CommandOutput process(CommandRequest command) {
		if(!command.hasArgument("id")) {
			return CommandOutputBuilder.build("There is no 'id' arguments in command: ''{0}''", command.getCommand()).get();
		}
		
		List<PlaylistDto> playlists = this.spotifyService.getCurrentUserPlaylists();

		if(playlists.isEmpty()) {
			return CommandOutputBuilder.build("No playlists found").get();
		} else if(this.deviceConfigurationService.isDeviceRegistered()) {
			return CommandOutputBuilder.build("No device registered").get();
		} else {
			Optional<PlaylistDto> playlist = playlists.stream().filter(p -> p.getId().equals(command.getArgument("id"))).findFirst();
			if(playlist.isPresent()) {
				this.spotifyServiceOrchestrator.playPlaylist(playlist.get().getId());
				return CommandOutputBuilder.build("Start playlist: ''{0}'' - name: ''{1}''", command.getArgument("id"), playlist.get().getName()).get();
			} else {
				return CommandOutputBuilder.build("No playlists found for id: ''{0}''", command.getArgument("id")).get();
			}
		}
	}
}
