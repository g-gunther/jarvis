package com.gguproject.jarvis.plugin.weather.command;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.display.event.DisplayEventData;
import com.gguproject.jarvis.plugin.weather.service.WeatherForecast;
import com.gguproject.jarvis.plugin.weather.service.WeatherService;

import javax.inject.Named;

@Named
@Qualifier(WeatherCommandProcessor.qualifier)
public class WeatherGetCommandProcessor extends AbstractCommandProcessor{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(WeatherGetCommandProcessor.class);

	private final WeatherService weatherService;

	private final EventBusService eventBusService;

	public WeatherGetCommandProcessor(WeatherService weatherService, EventBusService eventBusService) {
		super("get", "Get weather for a location");
		this.weatherService = weatherService;
		this.eventBusService = eventBusService;
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		if(!command.hasArgument("localization")) {
			return CommandOutputBuilder.build("There is no 'localization' arguments in command: ''{0}''", command.getCommand()).get();
		}

		WeatherForecast forecast = this.weatherService.getForecast(command.getArgument("localization"));
		this.eventBusService.externalEmit(new DisplayEventData("weather_small", forecast));
//weather get -localization=paris
		return CommandOutputBuilder.build("Command executed").get();
	}
}
