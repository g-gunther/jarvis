package com.gguproject.jarvis.plugin.speech.interpreter.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.gguproject.jarvis.plugin.speech.grammar.dto.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.SpeechContext;

/**
 * Process the speech to find the action
 */
public class ActionProcessor extends AbstractInterpreterProcessor {

	public static final String name = "actions";
	
	@Override
	public void process(Grammar grammar, SpeechContext context) throws InterpreterException {
		StringBuilder sb = new StringBuilder();
		
		Map<String, List<String>> potentialActions = new HashMap<>();
		
		while(context.hasNext()) {
			if(sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(context.getNext());
			
			Map<String, List<String>> foundActions = grammar.findActionsTypeByWord(sb.toString());
			// only 1 action type found
			if(foundActions.size() == 1) {
				potentialActions = foundActions;
				
				Entry<String, List<String>> foundAction = foundActions.entrySet().iterator().next();
				List<String> actionWords = foundAction.getValue();
				// there is only 1 action word
				if(actionWords.size() == 1) {
					// the action word match! -> move to the next processor
					if(actionWords.get(0).equals(sb.toString())) {
						context.setAction(foundAction.getKey());
						return;
					}
				}
			} else if(foundActions.size() > 1) {
				potentialActions = foundActions;
			} else {
				if(potentialActions.isEmpty()) {
					throw new InterpreterException("Not able to find the action type as first part of the speech {0}", context.getSpeech());
				} else {
					// some potential actions were found, it means it was matching before
					// so remove the last word, and try to find the action type with an exact match
					// and decrement the index of word iterator
					String previousWord = sb.toString().substring(0, sb.toString().lastIndexOf(" "));
					
					
					Map<String, List<String>> filteredActions = potentialActions.entrySet().stream()
						.filter(e -> e.getValue().contains(previousWord))
						.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
					if(filteredActions.size() == 1) {
						context.setAction(filteredActions.entrySet().iterator().next().getKey());
						context.setWordIteratorIndex(context.getWordIteratorIndex() - 1);
						return;
					} else {
						throw new InterpreterException("Not able to find the action type as first part of the speech {0}", context.getSpeech());
					}
				}
			}
		}
		
		throw new InterpreterException("Not able to find the action type as first part of the speech {0}", context.getSpeech());
	}
}
