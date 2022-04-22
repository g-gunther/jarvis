package com.gguproject.jarvis.plugin.speaker;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class SpeakerPluginConfiguration extends AbstractPluginConfiguration {

    public SpeakerPluginConfiguration() {
        super("jarvis-plugin-speaker-google");
    }

    public class PropertyKey {
        public static final String propertiesLanguage = "speaker.properties.language";

        public static final String googleCredentialPath = "speaker.google.credential";
        public static final String googleLanguage = "speaker.google.language";
        public static final String googleVoiceName = "speaker.google.voice.name";
        public static final String googleCounterFileName = "google.counter.filename";
    }
}
