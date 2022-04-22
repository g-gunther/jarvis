package com.gguproject.jarvis.plugin.weather.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import com.gguproject.jarvis.plugin.weather.service.WeatherCode;
import com.gguproject.jarvis.plugin.weather.service.WeatherForecast;
import com.gguproject.jarvis.plugin.weather.service.WeatherForecastData;

public class WeatherBitForecast {

	public WeatherForecast toWeatherForecast() {
		WeatherForecast forecast = new WeatherForecast(this.city_name);
		data.forEach(forecastData -> forecast.addForecast(forecastData.toForecastData()));
		return forecast;
	}
	
	private List<WeatherBitForecastData> data;

	/** city name */
	private String city_name;

	/** doubleitude */
	private double lon;

	/** latitude */
	private double lat;

	/** timezone */
	private String timezone;

	/** country code */
	private String country_code;

	/** state code */
	private String state_code;

	public class WeatherBitForecastData {

		public WeatherForecastData toForecastData() {
			return new WeatherForecastData()
				.averageTemperature(this.temp)
				.lowestTemperature(this.low_temp)
				.highestTemperature(this.high_temp)
				.date(LocalDate.parse(this.valid_date))
				.sunriseTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(this.sunrise_ts), TimeZone.getDefault().toZoneId()))
				.sunsetTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(this.sunset_ts), TimeZone.getDefault().toZoneId()))
				.precipitationProbablity(this.pop)
				.windSpeed(this.wind_spd)
				.weather(WeatherCode.findByCode(this.weather.code).orElseThrow(() -> new IllegalStateException("Weather code not found: " + this.weather.code)));
		}
		
		/** Moonrise time unix timestamp (UTC) */
		private int moonrise_ts;
		
		/** Abbreviated wind direction */
		private String wind_cdir;
		
		/** Average relative humidity (%) */
		private int rh;
		
		/** Average pressure (mb) */
		private double pres;
		
		/** High Temperature - Calculated from 6AM to 6AM local time (default Celcius) */
		private double high_temp;
		
		/** Sunset time unix timestamp (UTC) */
		private int sunset_ts;
		
		/** Average Ozone (Dobson units) */
		private double ozone;
		
		/** Moon phase illumination fraction (0-1) */
		private double moon_phase;
		
		/** Wind gust speed (Default m/s) */
		private double wind_gust_spd;
		
		/** Snow Depth (default mm) */
		private int snow_depth;
		
		/** Average total cloud coverage (%) */
		private int clouds;
		
		/** Forecast period start unix timestamp (UTC) */
		private int ts;
		
		/** Sunset time unix timestamp (UTC) */
		private int sunrise_ts;
		
		/** Apparent/"Feels Like" temperature at min_temp time (default Celcius) */
		private double app_min_temp;
		
		/** Wind speed (Default m/s) */
		private double wind_spd;
		
		/** Probability of Precipitation (%) */
		private int pop;
		
		/** Verbal wind direction */
		private String wind_cdir_full;
		
		/** Average sea level pressure (mb) */
		private double slp;
		
		/** Moon lunation fraction (0 = New moon, 0.50 = Full Moon, 0.75 = Last quarter moon) */
		private double moon_phase_lunation;
		
		/** Date the forecast is valid for in format YYYY-MM-DD [Midnight to midnight local time] */
		private String valid_date;
		
		/** Apparent/"Feels Like" temperature at max_temp time (default Celcius) */
		private double app_max_temp;
		
		/** Visibility (default KM) */
		private double vis;
		
		/** Average dew point (default Celcius) */
		private double dewpt;
		
		/** Accumulated snowfall (default mm) */
		private int snow;
		
		/** Maximum UV Index (0-11+) */
		private double uv;
		
		/** Wind direction (degrees) */
		private int wind_dir;
		
		/** [DEPRECATED] Maximum direct component of solar radiation (W/m^2) */
		private int max_dhi;
		
		/** High-level (>5km AGL) cloud coverage (%) */
		private int clouds_hi;
		
		/** Accumulated liquid equivalent precipitation (default mm) */
		private double precip;
		
		/** Low Temperature - Calculated from 6AM to 6AM local (default Celcius) */
		private double low_temp;
		
		/** Maximum Temperature (default Celcius) */
		private double max_temp;
		
		/** Moonset time unix timestamp (UTC) */
		private int moonset_ts;
		
		/** [DEPRECATED] Forecast valid date (YYYY-MM-DD) */
		private String datetime;
		
		/** Average Temperature (default Celcius) */
		private double temp;
		
		/** Minimum Temperature (default Celcius) */
		private double min_temp;
		
		/** Mid-level (~3-5km AGL) cloud coverage (%) */
		private int clouds_mid;
		
		/** Low-level (~0-3km AGL) cloud coverage (%) */
		private int clouds_low;
		
		private WeatherBitForecastDataDescription weather;
		
		@Override
		public String toString() {
			return "WeatherBitForecastData [high_temp=" + high_temp + ", valid_date=" + valid_date + ", low_temp="
					+ low_temp + ", temp=" + temp + ", weather=" + weather + "]";
		}

		/**
		 * 
		 */
		public class WeatherBitForecastDataDescription {
			private String icon;
			private int code;
			private String description;

			public int getCode() {
				return this.code;
			}
			
			@Override
			public String toString() {
				return "[code=" + code + ", description=" + description + "]";
			}
		}
	}

	@Override
	public String toString() {
		return "WeatherBitForecast [data=" + data + ", city_name=" + city_name + "]";
	}
}