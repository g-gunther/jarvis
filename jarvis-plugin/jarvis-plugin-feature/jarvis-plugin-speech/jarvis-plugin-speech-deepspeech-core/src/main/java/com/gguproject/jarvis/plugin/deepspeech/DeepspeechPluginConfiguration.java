package com.gguproject.jarvis.plugin.deepspeech;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class DeepspeechPluginConfiguration extends AbstractPluginConfiguration {

    public DeepspeechPluginConfiguration() {
        super("jarvis-plugin-deepspeech");
    }

    public class PropertyKey {
        public static final String keyword = "keyword";
        public static final String threshold = "threshold";
        public static final String noisewords = "noisewords";
    }
}
