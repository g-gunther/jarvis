package com.gguproject.jarvis.plugin.weather;

import com.gguproject.jarvis.plugin.core.dev.AbstractPluginDevStartup;
import com.gguproject.jarvis.plugin.weather.service.WeatherForecast;
import com.gguproject.jarvis.plugin.weather.service.WeatherService;

import javax.inject.Named;

@Named
public class PluginDevStartup extends AbstractPluginDevStartup {

    public static void main(String[] args) {
        init("jarvis-plugin-weather");
    }

    private final WeatherService weatherService;

    public PluginDevStartup(WeatherService weatherService){
        this.weatherService = weatherService;
    }

    protected void process() {
        WeatherForecast forecast = this.weatherService.getForecast("Paris");
    }
}
