package com.gguproject.jarvis.plugin.androidtv.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.AbstractParentCommandProcessor;

import javax.inject.Named;
import java.util.List;

@Named
@Qualifier(AbstractCommandProcessor.rootQualifier)
public class TvCommandProcessor extends AbstractParentCommandProcessor {
	public static final String qualifier = "TvCommandProcessor";

	public TvCommandProcessor(@Qualifier(TvCommandProcessor.qualifier) List<AbstractCommandProcessor> processors) {
		super("tv", "All tv related commands", processors);
	}
}
