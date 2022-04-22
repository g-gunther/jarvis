package com.gguproject.jarvis.plugin.speech.interpreter.processor;

import java.util.Optional;

import javax.inject.Named;

import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.processor.helper.SpaceAndTimeHelper;

/**
 * Process the process to find localization
 */
public class LocalizationProcessor extends AbstractInterpreterProcessor {

	public static final String name = "localization";

	@Override
	public void process(Grammar grammar, SpeechContext context) throws InterpreterException {
		new SpaceAndTimeHelper(grammar, context) {

			@Override
			protected boolean checkWordAndProcess(String delimiterWord, String word) {
				// check if the found substring speech is a localization or not
				Optional<String> localization = this.grammar.findLocalizationByWord(word);
				if(localization.isPresent()) {
					this.context.addLocalization(localization.get());
					return true;
				}
				
				return false;
			}
			
		}.process();
	}
}
