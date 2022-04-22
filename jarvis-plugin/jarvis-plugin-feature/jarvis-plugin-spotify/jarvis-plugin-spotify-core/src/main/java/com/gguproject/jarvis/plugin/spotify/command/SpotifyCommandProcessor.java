package com.gguproject.jarvis.plugin.spotify.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.AbstractParentCommandProcessor;

import javax.inject.Named;
import java.util.List;

@Named
@Qualifier(AbstractCommandProcessor.rootQualifier)
public class SpotifyCommandProcessor extends AbstractParentCommandProcessor {
	public static final String qualifier = "SpotifyCommandProcessor";

	public SpotifyCommandProcessor(@Qualifier(SpotifyCommandProcessor.qualifier) List<AbstractCommandProcessor> processors) {
		super("spotify", "All music related commands", processors);
	}
}
