package com.gguproject.jarvis.plugin.freeboxv6.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.AbstractParentCommandProcessor;

import javax.inject.Named;
import java.util.List;

@Named
@Qualifier(AbstractCommandProcessor.rootQualifier)
public class FreeboxCommandProcessor extends AbstractParentCommandProcessor {
	public static final String qualifier = "FreeboxCommandProcessor";

	public FreeboxCommandProcessor(@Qualifier(FreeboxCommandProcessor.qualifier) List<AbstractCommandProcessor> processors) {
		super("freebox", "All freebox related commands", processors);
	}
}