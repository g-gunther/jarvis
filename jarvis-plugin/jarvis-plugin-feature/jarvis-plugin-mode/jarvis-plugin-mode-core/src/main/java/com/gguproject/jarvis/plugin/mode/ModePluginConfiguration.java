package com.gguproject.jarvis.plugin.mode;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class ModePluginConfiguration extends AbstractPluginConfiguration {

    public ModePluginConfiguration() {
        super("jarvis-plugin-mode");
    }

    public class PropertyKey {
    }
}
