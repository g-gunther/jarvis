package com.gguproject.jarvis.plugin.google.speech;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;

import javax.inject.Named;

@Named
public class SpeechPluginConfiguration extends AbstractPluginConfiguration {

    public SpeechPluginConfiguration() {
        super("jarvis-plugin-speech-google");
    }

    public class PropertyKey {
        public static final String googleCredentialPath = "speech.google.credential";
        public static final String googleListeningTime = "speech.google.listeningTime";
        public static final String googleLanguage = "speech.google.language";
        public static final String googleCounterFileName = "google.counter.filename";
        public static final String googleDisabled = "speech.google.disabled";

        public static final String sphinxAcousticModelPath = "speech.sphinx.acousticModelPath";
        public static final String sphinxDictionaryPath = "speech.sphinx.dictionaryPath";
        public static final String sphinxGrammarPath = "speech.sphinx.grammarPath";
        public static final String sphinxGrammarName = "speech.sphinx.grammarName";
        public static final String sphinxConfiguration = "speech.sphinx.configuration";
    }
}
