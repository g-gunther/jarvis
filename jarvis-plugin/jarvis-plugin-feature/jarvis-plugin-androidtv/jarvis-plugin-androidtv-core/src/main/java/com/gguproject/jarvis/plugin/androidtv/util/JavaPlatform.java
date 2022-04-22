package com.gguproject.jarvis.plugin.androidtv.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class JavaPlatform {
	public static final int NAME = 0;
	public static final int CERTIFICATE_NAME = 1;
	public static final int UNIQUE_ID = 2; // needs to be unique per app so that multiple Anymote clients can run on the same device
	public static final int NETWORK_NAME = 3;
	public static final int MODE_PRIVATE = 0;
	
	private JavaPlatform() {
	}

	/**
	 * Open a file for output
	 * @param name
	 * @param mode
	 * @return
	 * @throws FileNotFoundException
	 */
	public static FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException { 
		return new FileOutputStream(name);
	}
	
	/**
	 * Open a file for input
	 * @param name
	 * @return
	 * @throws FileNotFoundException
	 */
	public static FileInputStream openFileInput(String name) throws FileNotFoundException {
		return new FileInputStream(name);
	}
	
	/**
	 * Get platform strings
	 * @param id
	 * @return
	 */
	public static String getString(int id) {
		switch (id) {
			case NAME:
				return "Java";
			case CERTIFICATE_NAME: 
				return "java";
			case UNIQUE_ID: 
				return "emulator";  // needs to be unique per app so that multiple Anymote clients can run on the same device
			case NETWORK_NAME: 
				return "wired";  // (Wifi would be SSID)
			default:
				return null;
		}
	}
}
