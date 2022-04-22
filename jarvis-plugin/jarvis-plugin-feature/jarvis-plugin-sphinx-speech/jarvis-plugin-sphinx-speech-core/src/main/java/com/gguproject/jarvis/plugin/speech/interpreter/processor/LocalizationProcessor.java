package com.gguproject.jarvis.plugin.speech.interpreter.processor;

import javax.inject.Named;

import com.gguproject.jarvis.plugin.speech.grammar.dto.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;

/**
 * Process the process to find localization
 */
@Named
public class LocalizationProcessor extends AbstractInterpreterProcessor {

	public static final String name = "localization";

	@Override
	public void process(Grammar grammar, SpeechContext context) throws InterpreterException { 
		if(context.hasNext()) {
			String localisationWord = context.getNext();
			context.setLocalization(grammar.getLocalization().findLocalizationByWord(localisationWord));
		}	
	}
}
