package com.gguproject.jarvis.plugin.freeboxv6.event;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class InfraRedPluginConfiguration extends AbstractPluginConfiguration {

    public InfraRedPluginConfiguration() {
        super("jarvis-plugin-infrared");
    }

    public class PropertyKey {
        public static final String configurationFile = "configuration.file";
    }
}
