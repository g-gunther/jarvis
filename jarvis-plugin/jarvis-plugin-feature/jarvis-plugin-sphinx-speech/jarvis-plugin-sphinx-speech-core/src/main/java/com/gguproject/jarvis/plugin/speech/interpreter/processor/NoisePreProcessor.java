package com.gguproject.jarvis.plugin.speech.interpreter.processor;

import java.util.List;

import com.gguproject.jarvis.plugin.speech.grammar.dto.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;

/**
 * Used to clean speech of noise words and keyword
 */
public class NoisePreProcessor extends AbstractInterpreterPreProcessor {

	@Override
	public void process(Grammar grammar, SpeechContext context) {
		List<String> noiseWords = grammar.getNoiseWords();
		
		// remove all noise words & keyword
		context.filter(w -> !noiseWords.contains(w) && !grammar.getKeyword().equals(w));
	}
	
}
