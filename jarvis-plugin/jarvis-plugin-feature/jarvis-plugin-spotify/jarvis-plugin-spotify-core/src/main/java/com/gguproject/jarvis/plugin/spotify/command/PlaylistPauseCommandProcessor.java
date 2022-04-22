package com.gguproject.jarvis.plugin.spotify.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.spotify.service.SpotifyService;

import javax.inject.Named;

/**
 * start playing a given playlist on the registered device
 */
@Named
@Qualifier(PlaylistCommandProcessor.qualifier)
public class PlaylistPauseCommandProcessor extends AbstractCommandProcessor {

	private final SpotifyService spotifyService;

	public PlaylistPauseCommandProcessor(SpotifyService spotifyService) {
		super("pause", "Pause the music");
		this.spotifyService = spotifyService;
	}

	@Override
	public CommandOutput process(CommandRequest command) {
		this.spotifyService.pause();
		return CommandOutputBuilder.build("Music pause").get();
	}
}
