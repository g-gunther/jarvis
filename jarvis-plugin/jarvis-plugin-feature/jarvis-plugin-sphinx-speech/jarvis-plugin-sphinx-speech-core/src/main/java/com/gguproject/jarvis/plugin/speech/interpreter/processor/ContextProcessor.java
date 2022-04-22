package com.gguproject.jarvis.plugin.speech.interpreter.processor;

import javax.inject.Named;

import com.gguproject.jarvis.plugin.speech.grammar.dto.Grammar;
import com.gguproject.jarvis.plugin.speech.grammar.dto.GrammarContext;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;

/**
 * Process the speech to find potential contexts
 */
@Named
public class ContextProcessor extends AbstractInterpreterProcessor {

	public static final String name = "context";

	@Override
	public void process(Grammar grammar, SpeechContext context) {
		int currentIndex = context.getWordIteratorIndex();
		
		GrammarContext foundContext = grammar.findContextByWordAndAction(context.getNext(), context.getAction());
		if(foundContext != null) {
			context.addPotentialContext(foundContext);
		} else {
			context.setWordIteratorIndex(currentIndex);
		}
	}
}
