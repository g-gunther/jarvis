package com.gguproject.jarvis.core.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.dto.JarNameProperties;

/**
 * Utilities related to the system
 */
public class SystemUtils {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SystemUtils.class);
	
	/**
	 * Check if the application has been loaded by a jar or not
	 * @return
	 */
	public static boolean isLoadedByJar() {
		File currentJar;
		try {
			currentJar = new File(SystemUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Error while retrieving the current jar file", e);
		}
		
		return !currentJar.getName().endsWith(".jar");
	}
	
	/**
	 * Get the main jar file which is the main entry point of the application
	 * @return
	 */
	public static File getCurrentJarFile() {
		File currentJar;
		try {
			currentJar = new File(SystemUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Error while retrieving the current jar file", e);
		}
		
		// check if the current jar is classes (for development purpose) or ends with .jar
		// else it's an issue...
		if(!currentJar.getName().equals("classes") && !currentJar.getName().endsWith(".jar")) {
			throw new IllegalStateException("The found application to restart is not a jar file: " + currentJar.getName());
		}
		return currentJar;
	}
	
	/**
	 * Restart the application
	 */
	public static void restart() {
		LOGGER.debug("Start restarting the app");
		
		final String javaBin = new StringBuilder()
				.append(System.getProperty("java.home"))
				.append(File.separator)
				.append("bin")
				.append(File.separator)
				.append("java")
				.toString();
		try {
			File jarFile = null;
			// find the file to start (it might be a new one, not necessarily the same as the current one)
			JarNameProperties jarNameProperties = JarUtils.parseFileName(SystemUtils.getCurrentJarFile().getName());
			for (File file : new File(".").listFiles()) {
			    if (file.isFile() && file.getName().startsWith(jarNameProperties.getName())) {
			        jarFile = file;
			        break;
			    }
			}

			if(jarFile == null) {
				throw new IllegalStateException("Not able to find the jar to restart");
			}

			final ArrayList<String> command = new ArrayList<String>();
			command.add(javaBin);
			command.add("-jar");
			command.add(jarFile.getPath());

			LOGGER.debug("Restart command to process: {}", command);
			final ProcessBuilder builder = new ProcessBuilder(command);
			builder.start();
			
			SystemUtils.exit();
		} catch (IOException e) {
			LOGGER.error("Error while trying to restart the current application", e);
		}
	}
	
	/**
	 * Exit the application
	 */
	public static void exit() {
		LOGGER.debug("Exit the current application");
		System.exit(0);
	}
}
