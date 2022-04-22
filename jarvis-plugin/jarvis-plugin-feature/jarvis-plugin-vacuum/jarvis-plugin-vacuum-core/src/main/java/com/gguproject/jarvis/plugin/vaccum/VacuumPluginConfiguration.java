package com.gguproject.jarvis.plugin.vaccum;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class VacuumPluginConfiguration extends AbstractPluginConfiguration {

    public VacuumPluginConfiguration() {
        super("jarvis-plugin-vacuum");
    }

    public class PropertyKey {
    }
}
