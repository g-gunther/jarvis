package com.gguproject.jarvis.core.utils;

public class DevUtils {

	public static void setDevEnvironment(){
		System.setProperty("env", "dev");
	}

	public static boolean isDevEnvironment() {
		return System.getProperty("env", "prod").equals("dev");
	}
}
