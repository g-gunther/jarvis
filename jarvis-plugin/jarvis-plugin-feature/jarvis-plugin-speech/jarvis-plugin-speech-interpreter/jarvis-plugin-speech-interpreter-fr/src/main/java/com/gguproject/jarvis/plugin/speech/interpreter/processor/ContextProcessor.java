package com.gguproject.jarvis.plugin.speech.interpreter.processor;

import javax.inject.Named;

import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.GrammarContext;

/**
 * Process the speech to find potential contexts
 */
@Named
public class ContextProcessor extends AbstractInterpreterProcessor {

	public static final String name = "context";

	@Override
	public void process(Grammar grammar, SpeechContext context) {
		int currentIndex = context.getWordIteratorIndex();
		
		// find the next word which is not a noise
		String word = null;
		while(context.hasNext()) {
			word = context.getNext();
			if(grammar.isNoiseWord(word) || grammar.isSpaceAndTimeWordDelimiter(word)) {
				continue;
			}
			break;
		}
		
		GrammarContext foundContext = grammar.findContextByWordAndAction(word, context.getAction());
		if(foundContext != null) {
			context.addPotentialContext(foundContext);
		} else {
			context.setWordIteratorIndex(currentIndex);
		}
	}
}
