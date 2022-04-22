package com.gguproject.jarvis.plugin.weather.service;

public interface WeatherService {

	/**
	 * 
	 * @param city
	 * @return
	 * @throws WeatherForecastException 
	 */
	public WeatherForecast getForecast(String city) throws WeatherForecastException;
}
