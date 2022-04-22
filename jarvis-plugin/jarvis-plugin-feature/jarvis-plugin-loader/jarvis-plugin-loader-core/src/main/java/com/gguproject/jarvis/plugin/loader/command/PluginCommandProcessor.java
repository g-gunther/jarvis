package com.gguproject.jarvis.plugin.loader.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.AbstractParentCommandProcessor;

import javax.inject.Named;
import java.util.List;

@Named
@Qualifier(AbstractCommandProcessor.rootQualifier)
public class PluginCommandProcessor extends AbstractParentCommandProcessor {
	public static final String qualifier = "PluginCommandProcessor";

	public PluginCommandProcessor(@Qualifier(PluginCommandProcessor.qualifier) List<AbstractCommandProcessor> processors) {
		super("plugin", "All plugin related commands (list, refresh, add, remove...)", processors);
	}
}
