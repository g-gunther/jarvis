package com.gguproject.jarvis.core.utils;

public class StringUtils {

	public static final String EMPTY = "";
	
	public static boolean isEmpty(String value) {
		return value == null || value.equals(EMPTY);
	}
	
	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}
	
	public static String leftpad(String text, int length) {
	    return String.format("%" + length + "." + length + "s", text);
	}

	public static String rightpad(String text, int length) {
	    return String.format("%-" + length + "." + length + "s", text);
	}
}
