package com.gguproject.jarvis.plugin.speech.dev;

import com.gguproject.jarvis.plugin.core.dev.AbstractPluginDevStartup;
import com.gguproject.jarvis.plugin.speech.sphinx.SphinxGrammarFileWriter;

import javax.inject.Named;

@Named
public class PluginDevStartup extends AbstractPluginDevStartup {

    public static void main(String[] args) {
        init("jarvis-plugin-speech");
    }

    private final SphinxGrammarFileWriter sphinxGrammarFileWriter;

    public PluginDevStartup(SphinxGrammarFileWriter sphinxGrammarFileWriter){
        this.sphinxGrammarFileWriter = sphinxGrammarFileWriter;
    }

    protected void process() {
        this.sphinxGrammarFileWriter.generate();
    }
}
