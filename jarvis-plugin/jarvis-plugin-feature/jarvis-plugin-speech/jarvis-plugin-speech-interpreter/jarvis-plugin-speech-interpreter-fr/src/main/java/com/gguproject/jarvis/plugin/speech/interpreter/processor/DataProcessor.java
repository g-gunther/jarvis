package com.gguproject.jarvis.plugin.speech.interpreter.processor;

import java.util.Map;

import javax.inject.Named;

import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.GrammarWord;

/**
 * Process the process to find data
 * If not context was found previously, it may help to identify it
 */
@Named
public class DataProcessor extends AbstractInterpreterProcessor {

	public static final String name = "data";

	@Override
	public void process(Grammar grammar, SpeechContext context) throws InterpreterException {
		// find the type of data
		// since this is the last word processor, get all the remaining words of the speech & process them
		String speechData = this.getRemainingDataSpeech(context);
		
		Map<String, GrammarWord> foundDataType = grammar.findDataType(speechData);
		final Data foundData = foundDataType.size() != 1 ? null : new Data(foundDataType.keySet().iterator().next(), foundDataType.values().iterator().next().getFullWord());
		
		if(context.isPotentialContextFound()) {
			// no data matching the given grammar
			if(foundData == null){
				// if only 1 context found, set the speech data as is, it will be analyzed by speech listeners (might be dynamic data analyzer)
				if(context.getPotentialContexts().size() == 1) {
					context.setData(speechData);
				} else {
					throw new InterpreterException("No data and no context found in speech: {0}", context.getSpeech()) ;
				}
			} 
			else {
				// check on already found contexts if for the found action, it might contains the type of data that has been found
				context.applyFilterOnPotentialContext(c -> c.getAction(context.getAction()).containsDataType(foundData.getType()));
				
				if(context.isPotentialContextFound()) {
					context.setData(foundData.getFoundValue());
				} else {
					// no able to find the data in potential context
					throw new InterpreterException("Not able to find data {0} in any of the found context in speech: {1}", foundData.getType(), context.getSpeech());	
				}
			}
		} else {
			// no data and no context -> definitely an error 
			if(foundData == null) {
				throw new InterpreterException("No data and no context found in speech: {0}", context.getSpeech()) ;
			}
			
			// try to find some contexts related to the given data
			context.addPotentialContexts(grammar.findContextByDataWordAndAction(foundData.getType(), context.getAction()));
			
			if(context.isPotentialContextFound()) {
				context.setData(foundData.getFoundValue());
			} else {
				// else nothing has been found -> error
				throw new InterpreterException("Not able to find any context or data related to the word {0} in speech: {1}", foundData.getFoundValue(), context.getSpeech());
			}
		}
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	private String getRemainingDataSpeech(SpeechContext context) {
		StringBuilder sb = new StringBuilder();
		while(context.hasNext()) {
			if(sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(context.getNext());
		}
		return sb.length() == 0 ? null : sb.toString();
	}
	
	private class Data {
		private String foundValue;
		private String type;
		public Data(String type, String foundValue) {
			this.foundValue = foundValue;
			this.type = type;
		}
		public String getType() {
			return this.type;
		}
		public String getFoundValue() {
			return this.foundValue;
		}
	}
}
