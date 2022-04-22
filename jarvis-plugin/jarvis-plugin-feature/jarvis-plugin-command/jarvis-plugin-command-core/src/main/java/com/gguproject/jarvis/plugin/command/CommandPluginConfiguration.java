package com.gguproject.jarvis.plugin.command;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class CommandPluginConfiguration extends AbstractPluginConfiguration {

    public CommandPluginConfiguration() {
        super("jarvis-plugin-infrared");
    }

    public class PropertyKey {
        public static final String configurationFile = "configuration.file";
    }
}
