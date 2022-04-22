package com.gguproject.jarvis.plugin.speech.sound;

import org.apache.commons.lang3.StringUtils;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speech.sound.encoder.Phonex;
import com.gguproject.jarvis.plugin.speech.sound.encoder.SoundEncoder;

public class SoundMatch {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SoundMatch.class);
	
	private static final SoundEncoder encoder = new Phonex();
	
	public static final  int threshold = 7;
	
	public static String encode(String value) {
		return encoder.encode(value);
	}
	
	public static int distance(String encodedReferenceValue, String encodedTestValue) {
		return StringUtils.getLevenshteinDistance(encodedReferenceValue, encodedTestValue);
	}
	
	/**
	 * 
	 * @param referenceValue
	 * @param testValue
	 * @return
	 */
	public static int calculateThreshold(String referenceValue, String testValue) {
		return (referenceValue.length() + testValue.length()) / SoundMatch.threshold;
	}
}
