package com.gguproject.jarvis.core.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

	public static <M, N> Map<M, N> getIntersection(Map<M, N> mapOne, Map<M, N> mapTwo) {
	    Map<M, N> intersection = new HashMap<>();
	    mapOne.keySet().stream().filter(key -> mapTwo.containsKey(key)).forEach(key -> {
	    	intersection.put(key, mapOne.get(key));
	    });
	    return intersection;
	}
}
