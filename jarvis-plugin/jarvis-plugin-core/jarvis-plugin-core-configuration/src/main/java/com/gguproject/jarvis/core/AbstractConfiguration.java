package com.gguproject.jarvis.core;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public abstract class AbstractConfiguration {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(AbstractConfiguration.class);

	private Map<String, String> properties = new HashMap<>();
	
	public InputStream getConfigurationResource(String filename) {
		ClassLoader classLoader = getClass().getClassLoader();
		return classLoader.getResourceAsStream(filename);
	}
	
	/**
	 * 
	 * @param file
	 */
	protected void loadProperties(File file) {
		try {
			this.loadProperties(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			LOGGER.error("Error while loading the application property file", e);
		}	
	}

	protected void loadProperties(InputStream is) {
		Properties prop = new Properties();
		try {
			prop.load(is);
			
			prop.entrySet().forEach(e -> {
				// if the property value is of type ${other.property.key} look for
				// this other.property.key to get its value
				String propertyValue = Optional.of((String) e.getValue())
						.filter(value -> value.startsWith("${") && value.endsWith("}"))
						.map(value -> value.substring(2, value.length() - 1))
						.map(placeholderKey -> this.properties.get(placeholderKey))
						.orElse((String) e.getValue());

				this.properties.putIfAbsent((String) e.getKey(), propertyValue);
			});
		} catch (IOException e) {
			LOGGER.error("Error while loading the application property file", e);
		}
	}
	
	public String getApplicationBasePath() {
		return Paths.get(".").toAbsolutePath().normalize().toString();
	}
	
	public Integer getIntProperty(String key) {
		return Integer.valueOf(this.getProperty(key));
	}
	
	public Boolean getBoolProperty(String key) {
		return Boolean.valueOf(this.getProperty(key));
	}
	
	public String getProperty(String key) {
		if(this.properties.containsKey(key)) {
			return this.properties.get(key);
		}
		throw new IllegalArgumentException("Property not found: " + key);
	}

	public String getLanguage(){
		return "fr-FR";
	}
}
