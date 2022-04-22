package com.gguproject.jarvis.plugin.cec;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class CecPluginConfiguration extends AbstractPluginConfiguration {

    public CecPluginConfiguration() {
        super("jarvis-plugin-cec");
    }

    public class PropertyKey {

    }
}
