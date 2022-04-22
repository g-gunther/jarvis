package com.gguproject.jarvis.plugin.spotify.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.AbstractParentCommandProcessor;

import javax.inject.Named;
import java.util.List;

@Named
@Qualifier(SpotifyCommandProcessor.qualifier)
public class DeviceCommandProcessor extends AbstractParentCommandProcessor {
	public static final String qualifier = "MusicDeviceCommandProcessor";

	public DeviceCommandProcessor(@Qualifier(DeviceCommandProcessor.qualifier) List<AbstractCommandProcessor> processors) {
		super("device", "All devices related commands", processors);
	}
}
