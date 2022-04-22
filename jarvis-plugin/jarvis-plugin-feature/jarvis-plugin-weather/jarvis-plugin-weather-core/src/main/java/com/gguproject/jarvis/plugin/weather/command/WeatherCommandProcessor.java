package com.gguproject.jarvis.plugin.weather.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.AbstractParentCommandProcessor;

import javax.inject.Named;
import java.util.List;

@Named
@Qualifier(AbstractCommandProcessor.rootQualifier)
public class WeatherCommandProcessor extends AbstractParentCommandProcessor {
	public static final String qualifier = "WeatherCommandProcessor";

	public WeatherCommandProcessor(@Qualifier(WeatherCommandProcessor.qualifier) List<AbstractCommandProcessor> processors) {
		super("weather", "All weather related commands", processors);
	}
}