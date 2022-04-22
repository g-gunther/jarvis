package com.gguproject.jarvis.plugin.loader;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class LoaderPluginConfiguration extends AbstractPluginConfiguration {

    public LoaderPluginConfiguration() {
        super("jarvis-plugin-loader");
    }

    public class PropertyKey {
    }
}
