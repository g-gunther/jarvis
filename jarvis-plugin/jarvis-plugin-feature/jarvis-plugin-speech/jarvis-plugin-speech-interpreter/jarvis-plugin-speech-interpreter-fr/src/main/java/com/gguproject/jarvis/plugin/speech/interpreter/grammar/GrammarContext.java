package com.gguproject.jarvis.plugin.speech.interpreter.grammar;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the grammar context
 */
public class GrammarContext {
	
	/**
	 * Context name
	 */
	private String type;

	/**
	 * List of words to identify this context
	 */
	private Set<GrammarWord> words;
	
	/**
	 * List of actions available for this context
	 */
	private List<GrammarContextAction> actions;

	public static GrammarContext build(GrammarContext context) {
		return new GrammarContext(context.getType(), 
			context.getWords().stream()
				.map(w -> w.getFullWord().toLowerCase())
				.collect(Collectors.toSet()), 
			context.getActions()
		);
	}
	
	public GrammarContext(String type, Set<String> words, List<GrammarContextAction> actions) {
		this.type = type;
		this.words = words.stream().map(GrammarWord::build).collect(Collectors.toSet());
		this.actions = actions;
	}

	/**
	 * Returns the list of words of this context
	 * @return
	 */
	public Collection<GrammarWord> getWords() {
		return Collections.unmodifiableSet(this.words);
	}

	/**
	 * Get the type of this context
	 * @return
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Returns the list of action available for this context
	 * @return
	 */
	public List<GrammarContextAction> getActions() {
		return Collections.unmodifiableList(actions);
	}
	
	/**
	 * Check if this context contains a given action
	 * @param word Action value
	 * @return True if found, false else
	 */
	public boolean containsAction(String word) {
		return this.actions
				.stream()
				.filter(e -> e.getAction().equals(word))
				.findFirst()
				.isPresent();
	}
	
	/**
	 * Check if the context contains a given data word and action
	 * @param word
	 * @param action
	 * @return
	 */
	public boolean containsDataTypeForAction(String dataType, String action) {
		return this.actions.stream()
			.filter(a -> a.getAction().equals(action) && a.containsDataType(dataType))
			.findFirst()
			.isPresent();
	}
	
	/**
	 * Find the action of this context by its name
	 * @param action
	 * @return
	 */
	public GrammarContextAction getAction(String action) {
		return this.actions.stream().filter(a -> a.getAction().equals(action)).findFirst().get();
	}
	
	/**
	 * Update the context values
	 * @param context
	 */
	public void updateContext(GrammarContext context) {
		this.words.addAll(context.words);
		
		context.getActions().forEach(actionToAdd -> {
			Optional<GrammarContextAction> o = this.actions.stream().filter(a -> a.getAction().equals(actionToAdd.getAction())).findFirst();
			if(o.isPresent()) {
				o.get().addData(actionToAdd.getData());
			} else {
				this.actions.add(actionToAdd);
			}
		});
	}

	@Override
	public String toString() {
		return "GrammarContext [type=" + type + ", words=" + words + ", actions=" + actions + "]";
	}
}
