package com.gguproject.jarvis.plugin.deepspeech.sound.service;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.deepspeech.DeepspeechPluginConfiguration;
import com.gguproject.jarvis.plugin.deepspeech.DeepspeechPluginConfiguration.PropertyKey;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * http://info.univ-lemans.fr/~carlier/recherche/soundex.html
 * https://yomguithereal.github.io/talisman/phonetics/french
 * @author guillaumegunther
 */
@Named
public class SoundCleaningService implements OnPostConstruct {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SoundCleaningService.class);
	
	private static final List<String> noiseWords = new ArrayList<>();

	private final DeepspeechPluginConfiguration configuration;

	public SoundCleaningService(DeepspeechPluginConfiguration configuration){
		this.configuration = configuration;
	}

	@Override
	public void postConstruct() {
		String noiseWordsConfig = configuration.getProperty(PropertyKey.noisewords);
		noiseWords.addAll(Arrays.asList(noiseWordsConfig.split(",")));
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public String clean(String value) {
		if(StringUtils.isEmpty(value)) {
			return value;
		}
		
		List<String> words = new ArrayList<>(Arrays.asList(value.split(" ")));
		words.removeAll(noiseWords);
		return StringUtils.join(words.toArray(), " ");
	}
}
