package com.gguproject.jarvis.plugin.loader.jarloader.updater;

import com.gguproject.jarvis.core.exception.BusinessException;

/**
 * Internal exception
 */
public class PluginJarException extends BusinessException {
    private static final long serialVersionUID = -8360773235711060284L;

    public PluginJarException(String message) {
        super(message);
    }

    public PluginJarException(String message, Exception e) {
        super(message, e);
    }
}
