package com.gguproject.jarvis.plugin.speech.interpreter.processor;

import java.util.List;

import javax.inject.Named;

import com.gguproject.jarvis.plugin.speech.grammar.dto.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;

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
		//----------> if several data match, try to get the next word?  
		Data foundData = this.getDataType(grammar, context);
		
		// no context found previously
		if(!context.isPotentialContextFound()) {
			// no data and no context -> definitely an error 
			if(foundData == null) {
				throw new InterpreterException("No data and no context found in speech: {0}", context.getSpeech()) ;
			}
			context.addPotentialContexts(grammar.findContextByDataWordAndAction(foundData.getType(), context.getAction()));
			if(context.isPotentialContextFound()) {
				context.setData(foundData.getValue());
			} else {
				// else nothing has been found -> error
				throw new InterpreterException("Not able to find any context or data related to the word {0} in speech: {1}", foundData.getValue(), context.getSpeech());
			}
		} else if(foundData != null){
			// check on already found contexts if the data may filter it
			context.filterPotentialContext(c -> c.getAction(context.getAction()).containsData(foundData.getType()));
			if(context.isPotentialContextFound()) {
				context.setData(foundData.getValue());
			} else {
				// no able to find the data in potential context
				throw new InterpreterException("Not able to find data {0} in any of the found context in speech: {1}", foundData.getType(), context.getSpeech());	
			}
		}
	}
	
	/**
	 * Analyse the speech to found a matching data type
	 * @param grammar
	 * @param context
	 * @return
	 * @throws InterpreterException
	 */
	private Data getDataType(Grammar grammar, SpeechContext context) throws InterpreterException {
		int currentIndex = context.getWordIteratorIndex();
		
		StringBuilder sb = new StringBuilder();
		// while there is words
		while(context.hasNext()) {
			if(sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(context.getNext());
			
			// try to find a data type that match
			List<String> foundDataType = grammar.findDataType(sb.toString());
			// if several datatype has been found, iterate to add another word
			if(foundDataType.size() > 1) {
				continue;
			} else if(foundDataType.size() == 1) {
				// datatype found! return it
				return new Data(foundDataType.get(0), sb.toString());
			}
		}
		
		// loop over all words is over, nothing found -> might mean it's somthing else -> move to the next processor
		// set back the iterator at this initial position
		context.setWordIteratorIndex(currentIndex);
		return null;
	}
	
	private class Data {
		private String value;
		private String type;
		public Data(String type, String value) {
			this.value = value;
			this.type = type;
		}
		public String getType() {
			return this.type;
		}
		public String getValue() {
			return this.value;
		}
	}
}
