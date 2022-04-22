package com.gguproject.jarvis.plugin.weather;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class WeatherPluginConfiguration extends AbstractPluginConfiguration {

    public WeatherPluginConfiguration() {
        super("jarvis-plugin-weather");
    }

    public class PropertyKey {
        public static final String weatherBitApiKey = "weatherbit.api.key";
        public static final String weatherBitApiEndpoint = "weatherbit.api.endpoint";
        public static final String weatherBitApiCountry = "weatherbit.api.country";
        public static final String weatherBitApiLang = "weatherbit.api.lang";
        public static final String weatherBitApiUnit = "weatherbit.api.unit";
    }
}
