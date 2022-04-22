package com.gguproject.jarvis.plugin.androidtv.service;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.androidtv.AndroidTvPluginConfiguration;
import com.gguproject.jarvis.plugin.androidtv.AndroidTvPluginConfiguration.PropertyKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import javax.inject.Named;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Named
public class TvChannelConverter implements OnPostConstruct {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(TvChannelConverter.class);

	private final AndroidTvPluginConfiguration configuration;
	
	private Map<Integer, List<String>> channels;

	public TvChannelConverter(AndroidTvPluginConfiguration configuration){
		this.configuration = configuration;
	}

	public void postConstruct() {
		Gson gson = new Gson();
		
		Optional<File> speechFile = this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.tvChannelFile));
		if(speechFile.isPresent()) {
			JsonReader reader;
			try {
				reader = new JsonReader(new FileReader(speechFile.get()));
				Type type = new TypeToken<Map<Integer, List<String>>>(){}.getType();
				this.channels = gson.fromJson(reader, type);
			} catch (FileNotFoundException e) {
				LOGGER.error("Can't find the given speech file: {}", speechFile.get().getName(), e);
				throw new TechnicalException("Not able to read the speech file " + speechFile.get().getAbsolutePath()) ;
			}
		}
	}
	
	public Integer findChannelByName(String channelName) {
		return this.channels.entrySet().stream()
			.filter(e -> e.getValue().contains(channelName))
			.map(Map.Entry::getKey)
			.findFirst()
			.orElse(null);
	}
}
