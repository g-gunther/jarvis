package com.gguproject.jarvis.plugin.speech;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class SpeechPluginConfiguration extends AbstractPluginConfiguration {

    public SpeechPluginConfiguration() {
        super("jarvis-plugin-speech");
    }

    public class PropertyKey {
        public static final String configuration = "speech.configuration";

        public static final String sphinxGrammarName = "speech.sphinx.grammarName";
        public static final String sphinxConfiguration = "speech.sphinx.configuration";
        public static final String sphinxAcousticModelPath = "speech.sphinx.acousticModelPath";
        public static final String sphinxDictionaryPath = "speech.sphinx.dictionaryPath";
        public static final String sphinxFullDictionaryPath = "speech.sphinx.fullDictionaryPath";
        public static final String sphinxGrammarPath = "speech.sphinx.grammarPath";
        public static final String sphinxGrammarFile = "speech.sphinx.grammarFile";
    }
}
