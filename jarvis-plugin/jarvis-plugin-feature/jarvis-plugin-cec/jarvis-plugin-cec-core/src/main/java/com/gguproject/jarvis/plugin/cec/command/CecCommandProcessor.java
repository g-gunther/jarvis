package com.gguproject.jarvis.plugin.cec.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.AbstractParentCommandProcessor;

import javax.inject.Named;
import java.util.List;

@Named
@Qualifier(AbstractCommandProcessor.rootQualifier)
public class CecCommandProcessor extends AbstractParentCommandProcessor {
	public static final String qualifier = "CecCommandProcessor";

	public CecCommandProcessor(@Qualifier(CecCommandProcessor.qualifier) List<AbstractCommandProcessor> processors) {
		super("cec", "All cec related commands", processors);
	}
}
