package com.gguproject.jarvis.core.ioc.context.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

/**
 * Indicates the order of the bean if several are found
 */
@Documented
@Retention(RUNTIME)
public @interface Order {
	int value();
}
