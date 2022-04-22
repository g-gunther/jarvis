package com.gguproject.jarvis.plugin.spotify.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.spotify.service.SpotifyService;
import com.gguproject.jarvis.plugin.spotify.service.dto.PlaylistDto;

import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

@Named
@Qualifier(PlaylistCommandProcessor.qualifier)
public class PlaylistListCommandProcessor extends AbstractCommandProcessor {

	private final SpotifyService spotifyService;

	public PlaylistListCommandProcessor(SpotifyService spotifyService) {
		super("list", "list all playlist of current user");
		this.spotifyService = spotifyService;
	}

	@Override
	public CommandOutput process(CommandRequest command) {
		List<PlaylistDto> playlists = this.spotifyService.getCurrentUserPlaylists();
		
		if(playlists.isEmpty()) {
			return CommandOutputBuilder.build("No playlists found").get();
		} else {
			return new CommandOutput(
				playlists.stream().map(p -> String.format("- '%s' with id: '%s'", p.getName(), p.getId()))
					.collect(Collectors.toList())
			);
		}
	}
}
