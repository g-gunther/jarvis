package com.gguproject.jarvis.plugin.weather.service;

import java.util.Optional;

/**
 * Type of weather 
 * (taken from https://www.weatherbit.io/api/codes)
 */
public enum WeatherCode {
	THUNDERSTORM_WITH_LIGHT_RAIN(200),
	THUNDERSTORM_WITH_RAIN(201),
	THUNDERSTORM_WITH_HEAVY_RAIN(202),
	THUNDERSTORM_WITH_LIGHT_DRIZZLE(230),
	THUNDERSTORM_WITH_DRIZZLE(231),
	THUNDERSTORM_WITH_HEAVY_DRIZZLE(232),
	THUNDERSTORM_WITH_HAIL(233),
	LIGHT_DRIZZLE(300),
	DRIZZLE(301),
	HEAVY_DRIZZLE(302),
	LIGHT_RAIN(500),
	MODERATE_RAIN(501),
	HEAVY_RAIN(502),
	FREEZING_RAIN(511),
	LIGHT_SHOWER_RAIN(520),
	SHOWER_RAIN(521),
	HEAVY_SHOWER_RAIN(522),
	LIGHT_SNOW(600),
	SNOW(601),
	HEAVY_SNOW(602),
	MIX_SNOW_RAIN(610),
	SLEET(611),
	HEAVY_SLEET(612),
	SNOW_SHOWER(621),
	HEAVY_SNOW_SHOWER(622),
	FLURRIES(623),
	MIST(700),
	SMOKE(711),
	HAZE(721),
	SAND_DUST(731),
	FOG(741),
	FREEZING_FOG(751),
	CLEAR_SKY(800),
	FEW_CLOUDS(801),
	SCATTERED_CLOUDS(802),
	BROKEN_CLOUDS(803),
	OVERCAST_CLOUDS(804),
	UNKNOWN_PRECIPITATION(900);
	
	private int code;
	
	private WeatherCode(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static Optional<WeatherCode> findByCode(int code){
		for(WeatherCode wc : values()) {
			if(wc.code == code) {
				return Optional.of(wc);
			}
		}
		return Optional.empty();
	}
}
