package com.gguproject.jarvis.plugin.weather.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeatherForecast {

	private String cityName;
	
	private List<WeatherForecastData> forecasts = new ArrayList<>();
	
	public WeatherForecast(String cityName) {
		this.cityName = cityName;
	}
	
	public String getCityName() {
		return this.cityName;
	}
	
	public List<WeatherForecastData> getForecasts(){
		return Collections.unmodifiableList(this.forecasts);
	}
	
	public void addForecast(WeatherForecastData data) {
		this.forecasts.add(data);
	}

	@Override
	public String toString() {
		return "WeatherForecast [cityName=" + cityName + ", forecasts=" + forecasts + "]";
	}
}
