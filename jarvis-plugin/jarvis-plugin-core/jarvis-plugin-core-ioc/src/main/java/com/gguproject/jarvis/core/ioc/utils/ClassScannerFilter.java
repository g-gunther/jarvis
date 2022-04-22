package com.gguproject.jarvis.core.ioc.utils;

@FunctionalInterface
public interface ClassScannerFilter {

	public boolean filter(Class<?> clazz);
}
