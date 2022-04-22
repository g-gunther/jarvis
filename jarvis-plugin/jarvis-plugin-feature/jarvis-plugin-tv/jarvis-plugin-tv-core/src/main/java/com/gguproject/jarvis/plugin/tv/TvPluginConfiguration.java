package com.gguproject.jarvis.plugin.tv;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class TvPluginConfiguration extends AbstractPluginConfiguration {

    public TvPluginConfiguration() {
        super("jarvis-plugin-tv");
    }

    public class PropertyKey {
        public final static String commandsConfigurationFile = "command.configuration.file";
    }
}
