package com.gguproject.jarvis.plugin.speech.interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.gguproject.jarvis.plugin.speech.interpreter.grammar.GrammarContext;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Time;
import com.gguproject.jarvis.plugin.speech.interpreter.support.TimeElement;

public class SpeechContext {
	
	/**
	 * Initial speech to analyse
	 */
	private String speech;
	
	/**
	 * List of words of the speech
	 */
	private List<String> initialWords;
	
	/**
	 * Iterator of the list of words 
	 * used to iterate over all words
	 */
	private WordIterator wordsIterator;
	
	/**
	 * Found action
	 */
	private String action;
	
	/**
	 * List of potential context that matches the action & data
	 */
	private List<GrammarContext> potentialContexts = new ArrayList<>();
	
	/**
	 * Data value
	 */
	private String data;
	
	/**
	 * Localization
	 */
	private List<String> localizations = new ArrayList<>();
	
	/**
	 * 
	 */
	private Time time = Time.build();
	
	public SpeechContext(String speech) {
		this.speech = speech;
		
		this.initialWords = Arrays.asList(speech.trim().split("\\s+"));
		this.wordsIterator = new WordIterator(this.initialWords);
	}
	
	/**
	 * Get the next word
	 * @return
	 */
	public String getNext() {
		return this.wordsIterator.next();
	}
	
	/**
	 * Is there any other word
	 * @return
	 */
	public boolean hasNext() {
		return this.wordsIterator.hasNext();
	}
	
	public int getWordIteratorIndex() {
		return this.wordsIterator.getIndex();
	}
	
	public void setWordIteratorIndex(int index) {
		this.wordsIterator.setIndex(index);
	}
	
	public void removeWords(int startIndex, int endIndex) {
		this.wordsIterator = this.wordsIterator.remove(startIndex, endIndex);
	}
	
	/**
	 * Filter the list of words
	 * @param filter
	 */
	public void filter(Predicate<String> filter) {
		this.initialWords = this.initialWords.stream().filter(filter).collect(Collectors.toList());
		this.wordsIterator = new WordIterator(this.initialWords);
	}
	
	/**
	 * Get speech
	 * @return
	 */
	public String getSpeech() {
		return this.speech;
	}
	
	/**
	 * Action
	 * @return
	 */
	public String getAction() {
		return this.action;
	}
	
	/**
	 * Set the action
	 * @param action Action
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * Add potential contexts
	 * @param context
	 */
	public void addPotentialContext(GrammarContext context) {
		this.potentialContexts.add(context);
	}
	
	/**
	 * Add potential contexts
	 * @param context
	 */
	public void addPotentialContexts(Collection<GrammarContext> contexts) {
		this.potentialContexts.addAll(contexts);
	}
	
	/**
	 * Check if potential context has been found
	 * @return
	 */
	public boolean isPotentialContextFound() {
		return !this.potentialContexts.isEmpty();
	}
	
	public void applyFilterOnPotentialContext(Predicate<? super GrammarContext> predicate) {
		this.potentialContexts = this.potentialContexts.stream().filter(predicate).collect(Collectors.toList());
	}
	
	public List<GrammarContext> getPotentialContexts() {
		return Collections.unmodifiableList(this.potentialContexts);
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public String getData() {
		return this.data;
	}
	
	public void addLocalization(String localization) {
		this.localizations.add(localization);
	}
	
	public List<String> getLocalizations() {
		return this.localizations;
	}
	
	public void addTimeElements(List<TimeElement> timeElements) {
		this.time.add(timeElements);
	}
	
	public Time getTime() {
		return this.time;
	}
	
	public String getRemainingSpeech() {
		return this.wordsIterator.toString();
	}
	
	/**
	 * Private class to navigate inside the words of the speech
	 */
	private class WordIterator {
		private List<String> words = new ArrayList<>();
		
		private int index = 0;
		
		public WordIterator(List<String> words) {
			this.words.addAll(words);
		}
		
		public WordIterator remove(int startIndex, int endIndex) {
			List<String> substrWords = new ArrayList<>();
			for(int i = 0; i < this.words.size(); i++) {
				if(i < startIndex || i >= endIndex) {
					substrWords.add(this.words.get(i));
				}
			}
			WordIterator newIterator = new WordIterator(substrWords);
			newIterator.index = startIndex;
			return newIterator;
		}
		
		public String next() {
			if(this.index == this.words.size()) {
				throw new IllegalStateException("No more value in the iterator");
			}
			
			String value =  this.words.get(this.index);
			this.index++;
			return value;
		}
		
		public boolean hasNext() {
			return this.index < this.words.size();
		}
		
		public int getIndex() {
			return this.index;
		}
		
		public void setIndex(int index) {
			if(index > this.words.size()) {
				throw new IllegalArgumentException("Can't set an index greater than the array size");
			}
			this.index = index;
		}
		
		@Override
		public String toString() {
			return String.join(" ", this.words);
		}
	}

	@Override
	public String toString() {
		return "SpeechContext [speech=" + speech + ", words=" + initialWords + ", action=" + action + ", potentialContexts="
				+ potentialContexts + ", data=" + data + ", localizations=" + localizations + "]";
	}
}
