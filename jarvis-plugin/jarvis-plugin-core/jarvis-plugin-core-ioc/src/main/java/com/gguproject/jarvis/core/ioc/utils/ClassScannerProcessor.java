package com.gguproject.jarvis.core.ioc.utils;

import java.util.Collection;

@FunctionalInterface
public interface ClassScannerProcessor {

	public void process(Collection<Class<?>> classes);
}
