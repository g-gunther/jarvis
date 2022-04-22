package com.gguproject.jarvis.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gguproject.jarvis.core.utils.dto.JarNameProperties;

public class JarUtils {

	private final static Pattern filenamePattern = Pattern.compile("^([\\w-]+)[-]([\\d\\w.-]+)[.]jar$");
	
	/**
	 * Parse a jar file name
	 * @param fileName Jar file name
	 * @return
	 */
	public static JarNameProperties parseFileName(String fileName) {
		Matcher matcher = filenamePattern.matcher(fileName);
		if(matcher.find()) {
			return new JarNameProperties(matcher.group(1), matcher.group(2));
		}
		throw new IllegalArgumentException("Can't parse given file name: " + fileName);
	}
}
