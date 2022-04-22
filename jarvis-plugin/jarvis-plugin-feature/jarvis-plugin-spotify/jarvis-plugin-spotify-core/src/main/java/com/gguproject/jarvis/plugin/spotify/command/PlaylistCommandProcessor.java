package com.gguproject.jarvis.plugin.spotify.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.AbstractParentCommandProcessor;

import javax.inject.Named;
import java.util.List;

@Named
@Qualifier(SpotifyCommandProcessor.qualifier)
public class PlaylistCommandProcessor extends AbstractParentCommandProcessor {
	public static final String qualifier = "MusicPlaylistCommandProcessor";

	public PlaylistCommandProcessor(@Qualifier(PlaylistCommandProcessor.qualifier) List<AbstractCommandProcessor> processors) {
		super("playlist", "All playlist related commands", processors);
	}
}
