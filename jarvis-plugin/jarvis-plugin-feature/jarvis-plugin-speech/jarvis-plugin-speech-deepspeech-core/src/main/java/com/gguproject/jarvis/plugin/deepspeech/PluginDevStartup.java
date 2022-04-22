package com.gguproject.jarvis.plugin.deepspeech;

import com.gguproject.jarvis.plugin.core.dev.AbstractPluginDevStartup;
import com.gguproject.jarvis.plugin.deepspeech.service.DeepspeechService;

import javax.inject.Named;

@Named
public class PluginDevStartup extends AbstractPluginDevStartup {

    public static void main(String[] args) {
        init("jarvis-plugin-deepspeech");
    }

    private final DeepspeechService deepspeechService;

    public PluginDevStartup(DeepspeechService deepspeechService){
        this.deepspeechService = deepspeechService;
    }

    protected void process() {
        // this.deepspeechService.run();
    }
}
