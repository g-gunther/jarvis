package com.gguproject.jarvis.plugin.speaker;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class SpeakerPluginConfiguration extends AbstractPluginConfiguration {

    public SpeakerPluginConfiguration() {
        super("jarvis-plugin-speaker");
    }

    public class PropertyKey {
        public static final String voice = "speaker.voice";
    }
}
