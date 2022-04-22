package com.gguproject.jarvis.plugin.spotify.service;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.spotify.SpotifyPluginConfiguration;
import com.gguproject.jarvis.plugin.spotify.SpotifyPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.spotify.service.dto.DeviceDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import javax.inject.Named;
import java.io.*;
import java.util.Optional;

/**
 * Managed the device configuration file
 * It contains the registered device which is used to play music
 * when receiving commands
 */
@Named
public class DeviceConfigurationService implements OnPostConstruct {
private static final Logger LOGGER = AbstractLoggerFactory.getLogger(DeviceConfigurationService.class);
	
	private Gson gsonParser;

	private final SpotifyPluginConfiguration configuration;
	
	private DeviceDto device;

	public DeviceConfigurationService(SpotifyPluginConfiguration configuration) {
		this.configuration = configuration;

		GsonBuilder gsonBuilder = new GsonBuilder();
		this.gsonParser = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();
	}
	
	@Override
	public void postConstruct() {
		this.parseConfigurationFile();
	}
	
	/**
	 * Parse the used configuration file
	 * @return
	 */
	private void parseConfigurationFile() {
		Optional<File> file = this.configuration.getSecretDataFile(this.configuration.getProperty(PropertyKey.spotifyDeviceFile));
		if(file.isPresent()) {
			JsonReader reader;
			try {
				reader = new JsonReader(new FileReader(file.get()));
				this.device = this.gsonParser.fromJson(reader, DeviceDto.class);
			} catch (FileNotFoundException e) {
				LOGGER.error("Can't find the given device file: {}", file.get().getName(), e);
				throw new IllegalStateException("Not able to read the device file " + file.get().getAbsolutePath()) ;
			}
		}
	}
	
	/**
	 * Write the configuration file
	 */
	private void writeConfigurationFile(DeviceDto device) {
		File file = this.configuration.getSecretDataFileOrCreate(this.configuration.getProperty(PropertyKey.spotifyDeviceFile));

		try (Writer writer = new FileWriter(file)) {
			this.gsonParser.toJson(device, writer);
		} catch (IOException e) {
			LOGGER.error("Can't write the device file: {}", file.getName(), e);
			throw new IllegalStateException("Not able to write the device file " + file.getAbsolutePath()) ;
		}
	}
	
	/**
	 * set the current device
	 * @param device
	 */
	public void setDevice(DeviceDto device) {
		this.writeConfigurationFile(device);
		this.device = device;
	}
	
	/**
	 * get current device
	 * @return
	 */
	public DeviceDto getDevice() {
		return this.device;
	}

	public boolean isDeviceRegistered(){
		return this.device != null;
	}
}
