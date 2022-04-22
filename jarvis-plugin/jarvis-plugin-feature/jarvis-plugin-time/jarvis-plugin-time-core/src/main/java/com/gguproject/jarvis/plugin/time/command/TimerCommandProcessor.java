package com.gguproject.jarvis.plugin.time.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.AbstractParentCommandProcessor;

import javax.inject.Named;
import java.util.List;

@Named
@Qualifier(AbstractCommandProcessor.rootQualifier)
public class TimerCommandProcessor extends AbstractParentCommandProcessor {
	public static final String qualifier = "TimerCommandProcessor";

	public TimerCommandProcessor(@Qualifier(TimerCommandProcessor.qualifier) List<AbstractCommandProcessor> processors) {
		super("timer", "All timer related commands", processors);
	}
}