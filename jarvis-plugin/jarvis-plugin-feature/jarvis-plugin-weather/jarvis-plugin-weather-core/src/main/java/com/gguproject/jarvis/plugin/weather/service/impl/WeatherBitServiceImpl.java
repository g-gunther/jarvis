package com.gguproject.jarvis.plugin.weather.service.impl;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.weather.WeatherPluginConfiguration;
import com.gguproject.jarvis.plugin.weather.WeatherPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.weather.service.WeatherForecast;
import com.gguproject.jarvis.plugin.weather.service.WeatherForecastException;
import com.gguproject.jarvis.plugin.weather.service.WeatherService;
import com.google.gson.Gson;

import javax.inject.Named;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

@Named
public class WeatherBitServiceImpl implements WeatherService {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(WeatherBitServiceImpl.class);
	
	private static Gson gson = new Gson();
	
	protected static final HttpClient httpClient = HttpClient.newHttpClient();

	private final WeatherPluginConfiguration configuration;

	public WeatherBitServiceImpl(WeatherPluginConfiguration configuration){
		this.configuration = configuration;
	}

	@Override
	public WeatherForecast getForecast(String city) throws WeatherForecastException {
		LOGGER.debug("Get weather foreacast for city: {}", city);
		
		StringBuilder url = new StringBuilder(this.configuration.getProperty(PropertyKey.weatherBitApiEndpoint))
			.append("forecast/daily?city=")
			.append(city)
			.append("&country=").append(this.configuration.getProperty(PropertyKey.weatherBitApiCountry))
			.append("&lang=").append(this.configuration.getProperty(PropertyKey.weatherBitApiLang))
			.append("&units=").append(this.configuration.getProperty(PropertyKey.weatherBitApiUnit))
			.append("&key=").append(this.configuration.getProperty(PropertyKey.weatherBitApiKey));

		LOGGER.debug("Call weatherbit api: {}", url.toString());
		
		HttpRequest request = HttpRequest.newBuilder()
			      .uri(URI.create(url.toString()))
			      .header("Content-Type", "application/json")
			      .GET()
			      .timeout(Duration.ofSeconds(2))
			      .build();
		try {
			HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
			
			if(response.statusCode() == 204) {
				throw new WeatherForecastException("No city found for: " + city);
			} else if(response.statusCode() == 200) {
				WeatherBitForecast weatherbitForecast = gson.fromJson(response.body(), WeatherBitForecast.class);
				return weatherbitForecast.toWeatherForecast();
			} else {
				throw new WeatherForecastException("Unexcepted response code from weather api: " + response.statusCode() + " - " + response.body());
			}
		} catch (IOException | InterruptedException e) {
			throw new WeatherForecastException("An error occurs while calling weather api", e);
		}
	}
}


