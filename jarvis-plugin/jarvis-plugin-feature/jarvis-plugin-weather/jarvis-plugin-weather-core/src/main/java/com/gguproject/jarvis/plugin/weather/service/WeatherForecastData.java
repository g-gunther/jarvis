package com.gguproject.jarvis.plugin.weather.service;

import com.gguproject.jarvis.core.jsonadapter.LocalDateSerializer;
import com.gguproject.jarvis.core.jsonadapter.LocalDateTimeSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WeatherForecastData {

	@JsonAdapter(LocalDateSerializer.class)
	private LocalDate date;

	private WeatherCode weather;
	private double highestTemperature;
	private double lowestTemperature;
	private double averageTemperature;

	@JsonAdapter(LocalDateTimeSerializer.class)
	private LocalDateTime sunriseTime;

	@JsonAdapter(LocalDateTimeSerializer.class)
	private LocalDateTime sunsetTime;

	private int precipitationProbablity;
	private double windSpeed;
	
	public LocalDate getDate() {
		return date;
	}
	
	public WeatherForecastData date(LocalDate date) {
		this.date = date;
		return this;
	}
	
	public WeatherCode getWeather() {
		return weather;
	}
	
	public WeatherForecastData weather(WeatherCode weather) {
		this.weather = weather;
		return this;
	}
	
	public double getHighestTemperature() {
		return highestTemperature;
	}
	
	public WeatherForecastData highestTemperature(double highestTemperature) {
		this.highestTemperature = highestTemperature;
		return this;
	}
	
	public double getLowestTemperature() {
		return lowestTemperature;
	}
	
	public WeatherForecastData lowestTemperature(double lowestTemperature) {
		this.lowestTemperature = lowestTemperature;
		return this;
	}
	
	public double getAverageTemperature() {
		return averageTemperature;
	}
	
	public WeatherForecastData averageTemperature(double averageTemperature) {
		this.averageTemperature = averageTemperature;
		return this;
	}

	public LocalDateTime getSunriseTime() {
		return sunriseTime;
	}

	public WeatherForecastData sunriseTime(LocalDateTime sunriseTime) {
		this.sunriseTime = sunriseTime;
		return this;
	}

	public LocalDateTime getSunsetTime() {
		return sunsetTime;
	}

	public WeatherForecastData sunsetTime(LocalDateTime sunsetTime) {
		this.sunsetTime = sunsetTime;
		return this;
	}

	public int getPrecipitationProbablity() {
		return precipitationProbablity;
	}

	public WeatherForecastData precipitationProbablity(int precipitationProbablity) {
		this.precipitationProbablity = precipitationProbablity;
		return this;
	}
	
	public WeatherForecastData windSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
		return this;
	}
	
	public double geWindSpeed() {
		return this.windSpeed;
	}

	@Override
	public String toString() {
		return "WeatherForecastData [date=" + date + ", weather=" + weather + ", highestTemperature="
				+ highestTemperature + ", lowestTemperature=" + lowestTemperature + ", averageTemperature="
				+ averageTemperature + ", sunriseTime=" + sunriseTime + ", sunsetTime=" + sunsetTime
				+ ", precipitationProbablity=" + precipitationProbablity + "]";
	}
}
