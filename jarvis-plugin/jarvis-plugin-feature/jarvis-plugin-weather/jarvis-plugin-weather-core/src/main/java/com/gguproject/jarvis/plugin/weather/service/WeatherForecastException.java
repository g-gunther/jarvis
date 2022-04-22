package com.gguproject.jarvis.plugin.weather.service;

import com.gguproject.jarvis.core.exception.BusinessException;

public class WeatherForecastException extends BusinessException {
	private static final long serialVersionUID = -4481066257484493555L;

	public WeatherForecastException(String message) {
		super(message);
	}
	
	public WeatherForecastException(String message, Throwable exception) {
		super(message, exception);
	}
}
