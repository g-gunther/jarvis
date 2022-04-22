package com.gguproject.jarvis.plugin.speech.interpreter.processor.helper;

import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.Grammar;

/**
 * Class used to parse a given speech based on a grammar
 * and extract localization or time informations
 */
public abstract class SpaceAndTimeHelper {
	protected Grammar grammar;
	protected SpeechContext context;
	
	/**
	 * Indicated if a localization or time delimiter has been found
	 */
	protected boolean delimiterFound = false;
	
	/**
	 * Delimiter word
	 */
	protected String delimiterWord;
	
	/**
	 * Index of the delimiter that has been found
	 */
	private int startDelimiterIndex = 0;
	
	
	protected StringBuilder temporarySpeech  = new StringBuilder(); 
	
	public SpaceAndTimeHelper(Grammar grammar, SpeechContext context) {
		this.grammar = grammar;
		this.context = context;
	}
	
	public void process() throws InterpreterException {
		wordLoop: while(this.context.hasNext()) {
			String word = this.context.getNext();
			
			switch(this.processWord(word)) {
				case BREAK:
					break wordLoop;
				case CONTINUE:
					continue wordLoop;
				case NONE: default:
					break;
			}
		}
		
		// there is a localization speech and we reached the end of the text
		this.processSpeech(this.context.getWordIteratorIndex());
		
		this.context.setWordIteratorIndex(0);
	}
	
	/**
	 * Check if a delimiter is found or not
	 * @param word
	 * @return
	 */
	protected boolean isDelimiterFound(String word) {
		return this.grammar.isSpaceAndTimeWordDelimiter(word);
	}
	
	/**
	 * Process a given word
	 * @param word
	 * @return
	 */
	protected ProcessResultAction processWord(String word) {
		// a delimiter has been found
		if(this.isDelimiterFound(word)) {
			// a delimiter has been previously found
			if(this.delimiterFound) {
				this.processSpeech(context.getWordIteratorIndex() - 1);
			}
			
			this.delimiterFound = true;
			this.delimiterWord = word;
			this.temporarySpeech.setLength(0);
			
			// because incremented when calling .getNext()
			this.startDelimiterIndex = context.getWordIteratorIndex() - 1; 
		} 
		else if(this.delimiterFound) {
			// get words until the next delimiter, or end of the speech, or an action word
			
			// noise word, move to the next word
			if(grammar.isNoiseWord(word)) {
				return ProcessResultAction.CONTINUE;
			} else if(grammar.isActionWord(word)) {
				this.processSpeech(context.getWordIteratorIndex() - 1);
				this.delimiterFound = false;
				this.temporarySpeech.setLength(0);
			} else if(grammar.isConcatWord(word)) { 
				this.processSpeech(context.getWordIteratorIndex());
				this.delimiterFound = true;
				this.temporarySpeech.setLength(0);
			} else {
				if(this.temporarySpeech.length() > 0) {
					this.temporarySpeech.append(" ");
				}
				this.temporarySpeech.append(word);
			}
		}
		
		return ProcessResultAction.NONE;
	}
	
	/**
	 * 
	 * @param endIndex
	 */
	protected void processSpeech(int endIndex) {
		if(this.temporarySpeech.length() > 0) {
			if(this.checkWordAndProcess(this.delimiterWord, this.temporarySpeech.toString())) {
				// remove words from the delimiter index and the current index
				this.context.removeWords(this.startDelimiterIndex, endIndex);
			}
		}
	}
	
	protected abstract boolean checkWordAndProcess(String delimiterWord, String word);
	
	protected enum ProcessResultAction {
		CONTINUE,
		BREAK,
		NONE;
	}
}
