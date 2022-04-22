package com.gguproject.jarvis.core.utils;

public class ArrayUtils {

	public static <T> boolean contains(T[] values, T value) {
		for(T v : values) {
			if(v.equals(value)) {
				return true;
			}
		}
		return false;
	}
}
