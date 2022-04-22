package com.gguproject.jarvis.plugin.speaker;

import com.gguproject.jarvis.plugin.core.dev.AbstractPluginDevStartup;

import javax.inject.Named;

@Named
public class PluginDevStartup extends AbstractPluginDevStartup {

    public static void main(String[] args) {
        init("jarvis-plugin-speaker");
    }

    private final SpeakerService speakerService;

    public PluginDevStartup(SpeakerService speakerService){
        this.speakerService = speakerService;
    }

    protected void process() {
        this.speakerService.speak("Bonjour. Temps ensoleillé aujourd'hui sur Paris.");
    }
}
