package com.gguproject.jarvis.plugin.speech.interpreter.processor;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import com.gguproject.jarvis.core.utils.MapUtils;
import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.GrammarWord;

/**
 * Process the speech to find the action
 */
public class ActionProcessor extends AbstractInterpreterProcessor {

	public static final String name = "actions";
	
	@Override
	public void process(Grammar grammar, SpeechContext context) throws InterpreterException {
		new ActionProcessorHelper(grammar, context).process();
	}
	
	/**
	 * Helper used to process the speech by having class attributes
	 */
	private class ActionProcessorHelper {
		/**
		 * list of temporary actions when several actions has been found during the process
		 */
		private Map<String, List<GrammarWord>> potentialActions = new HashMap<>();
		
		private Grammar grammar;
		private SpeechContext context;
		
		private StringBuilder wordSb = new StringBuilder();
		
		public ActionProcessorHelper(Grammar grammar, SpeechContext context) {
			this.grammar = grammar;
			this.context = context;
		}
		
		public void process() throws InterpreterException {
			while(this.context.hasNext()) {
				String word = this.context.getNext();
				
				// skip noise words
				if(this.grammar.isNoiseWord(word)) {
					continue;
				}
				// if a delimiter is found, it means that we reached the end of the action anyway
				else if(this.grammar.isSpaceAndTimeWordDelimiter(word) || this.grammar.isConcatWord(word)) {
					this.findActionFromPotentialActions();
					return;
				}
				
				// build incrementally the action word until one is found
				if(this.wordSb.length() > 0) {
					this.wordSb.append(" ");
				}
				this.wordSb.append(word);
				String testedWord = this.wordSb.toString();
				
				// find actions that match the word & based on previous potential actions found
				Map<String, List<GrammarWord>> actions = this.findActionsByWord(testedWord);
				
				// if there is only 1 action and only 1 action word for it
				// check that the words currently under analysis match it
				// if yes, it means the full action words has been found and we can move to the next processor
				if(actions.size() == 1) {
					this.potentialActions = actions;
					
					Entry<String, List<GrammarWord>> action = actions.entrySet().iterator().next();
					List<GrammarWord> actionWords = action.getValue();
					if(actionWords.size() == 1) {
						GrammarWord actionWord = actionWords.get(0);
						if(actionWord.exactMatch(testedWord) || actionWord.match(testedWord)){
							this.context.setAction(action.getKey());
							return;
						}
					}
					continue;
				}
				// several actions found - store them temporarily
				else if(actions.size() > 1) {
					this.potentialActions = actions;
					continue;
				} else {
					this.findActionFromPotentialActions();
					return;
				}
			}
			
			throw new InterpreterException("Not able to find the action type as first part of the speech {0}", this.context.getSpeech());
		}
		
		/**
		 * look for actions matching the word
		 * if there are some potential actions found from previous searches, keep only those that are in both list
		 * @param word
		 * @return
		 */
		private Map<String, List<GrammarWord>> findActionsByWord(String word){
			Map<String, List<GrammarWord>> foundActions = this.grammar.findActionsByWord(word);
			if(!this.potentialActions.isEmpty()) {
				return MapUtils.getIntersection(foundActions, this.potentialActions);
			}
			return foundActions;
		}
		
		/**
		 * some potential actions were found one previous integration, it means it was matching before
		 * so remove the last word, and try to find the action type with an exact match
		 * and decrement the index of word iterator in order to process it on next processor
		 * @throws InterpreterException
		 */
		private void findActionFromPotentialActions() throws InterpreterException {
			// no actions at all -> error
			if(this.potentialActions.isEmpty()) {
				throw new InterpreterException("Not able to find the action type as first part of the speech {0}", context.getSpeech());
			} else {
				// rebuild the previous tested words
				String previousTestWord = this.wordSb.toString().substring(0, this.wordSb.toString().lastIndexOf(" "));
				
				// build a list of [action type / action word] and sort them by their distance with the previous word
				// and keep only the first one
				Entry<String, GrammarWord> firstActionMatch = this.potentialActions.entrySet().stream()
					.flatMap(e -> {
						Builder<Entry<String, GrammarWord>> builder = Stream.builder();
						e.getValue().stream().map(v -> new AbstractMap.SimpleEntry<>(e.getKey(), v)).forEach(v -> builder.add(v));
						return builder.build();
					})
					.sorted((v1, v2) -> v1.getValue().distance(previousTestWord) < v2.getValue().distance(previousTestWord) ? -1 : 1)
					.findFirst()
					.orElseThrow(() -> new InterpreterException("Not able to find the action type as first part of the speech {0}", this.context.getSpeech()));
				
				this.context.setAction(firstActionMatch.getKey());
				this.context.setWordIteratorIndex(this.context.getWordIteratorIndex() - 1);
				return;
			}
		}
	}
}
