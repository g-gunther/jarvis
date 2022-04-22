package com.gguproject.jarvis.plugin.light;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class LightPluginConfiguration extends AbstractPluginConfiguration {

    public LightPluginConfiguration() {
        super("jarvis-plugin-light");
    }

    public class PropertyKey {
    }
}
