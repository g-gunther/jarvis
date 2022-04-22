package com.gguproject.jarvis.plugin.loader.command.data;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.AbstractParentCommandProcessor;
import com.gguproject.jarvis.plugin.loader.command.PluginCommandProcessor;

import javax.inject.Named;
import java.util.List;

@Named
@Qualifier(PluginCommandProcessor.qualifier)
public class PluginDataCommandProcessor extends AbstractParentCommandProcessor {
	public static final String qualifier = "PluginDataCommandProcessor";

	public PluginDataCommandProcessor(@Qualifier(PluginDataCommandProcessor.qualifier) List<AbstractCommandProcessor> processors) {
		super("data", "All plugin data related commands (refresh...)", processors);
	}
}
