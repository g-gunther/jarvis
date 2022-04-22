package com.gguproject.jarvis.plugin.weather.listener;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.display.event.DisplayEventData;
import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterService;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.listener.AsbtractSpeechCommandProcessor;
import com.gguproject.jarvis.plugin.weather.service.WeatherForecast;
import com.gguproject.jarvis.plugin.weather.service.WeatherService;

import javax.inject.Named;

@Named
public class WeatherSpeechCommandProcessor extends AsbtractSpeechCommandProcessor {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(WeatherSpeechCommandProcessor.class);

	private final EventBusService eventBusService;

	private final WeatherService weatherService;

	private final InterpreterService interpreterService;

	public WeatherSpeechCommandProcessor(EventBusService eventBusService, WeatherService weatherService, InterpreterService interpreterService) {
		super("WEATHER", "DISPLAY");
		this.eventBusService = eventBusService;
		this.weatherService = weatherService;
		this.interpreterService = interpreterService;
	}

	@Override
	public void process(DistributedEvent event, Command command) {
		String weatherLocalization = this.interpreterService.cleanDataSpeech(command.getData());

		WeatherForecast forecast = this.weatherService.getForecast(weatherLocalization);
		this.eventBusService.externalEmit(new DisplayEventData("weather_small", forecast));
	}
}
