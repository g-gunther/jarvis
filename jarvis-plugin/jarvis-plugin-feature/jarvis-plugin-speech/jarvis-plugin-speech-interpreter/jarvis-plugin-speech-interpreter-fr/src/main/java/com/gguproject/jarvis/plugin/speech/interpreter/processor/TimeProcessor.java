package com.gguproject.jarvis.plugin.speech.interpreter.processor;

import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.Grammar.GrammarFr;
import com.gguproject.jarvis.plugin.speech.interpreter.processor.helper.SpaceAndTimeHelper;
import com.gguproject.jarvis.plugin.speech.interpreter.processor.helper.SpeechToTimeService;
import com.gguproject.jarvis.plugin.speech.interpreter.support.TimeSpeechParserException;

/**
 * Process the process to find localization
 */
public class TimeProcessor extends AbstractInterpreterProcessor {

	public static final String name = "timer";

	@Override
	public void process(Grammar grammar, SpeechContext context) throws InterpreterException {
		SpeechToTimeService speechToTimeService = new SpeechToTimeService();
		
		new SpaceAndTimeHelper(grammar, context) {

			@Override
			protected boolean isDelimiterFound(String word) {
				return GrammarFr.exactLocalizationAndTimeDelimiter.contains(word) || this.grammar.isSpaceAndTimeWordDelimiter(word);
			}
			
			@Override
			protected boolean checkWordAndProcess(String delimiterWord, String word) {
				try {
					this.context.addTimeElements(speechToTimeService.analyse(word));
					if(GrammarFr.exactLocalizationAndTimeDelimiter.contains(delimiterWord)) {
						this.context.getTime().setExactTime();
					}
					return true;
				} catch (TimeSpeechParserException e) {
					return false;
				}
			}
			
			/**
			 * Override the standard processing to do specific checks on time
			 */
			@Override
			protected ProcessResultAction processWord(String word) {
				if(this.delimiterFound) {
					if(GrammarFr.Time.HourModifier.delimiter.contains(word) 
							|| GrammarFr.Time.HourModifier.morning.equals(word)
							|| GrammarFr.Time.HourModifier.afternoon.equals(word)
							|| GrammarFr.Time.HourModifier.evening.equals(word)) {
						if(this.temporarySpeech.length() > 0) {
							this.temporarySpeech.append(" ");
						}
						this.temporarySpeech.append(word);
						
						return ProcessResultAction.CONTINUE;
					}
				}
				
				return super.processWord(word);
			}
		}.process();
	}
}
