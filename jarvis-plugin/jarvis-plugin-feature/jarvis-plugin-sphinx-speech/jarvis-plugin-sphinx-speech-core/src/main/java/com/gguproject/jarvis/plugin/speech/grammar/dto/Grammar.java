package com.gguproject.jarvis.plugin.speech.grammar.dto;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the grammar loaded from the speech configuration file
 */
public class Grammar {
	
	/**
	 * Name of the grammar entry which is the final speech output
	 */
	public static final String GRAMMAR_OUPUT = "output";

	/**
	 * Speech keyword wich triggers the speech process
	 */
	private String keyword;
	
	/**
	 * List of data values
	 */
	private Map<String, Set<String>> data;
	
	/**
	 * List of all actions
	 */
	private Map<String, Set<String>> actions;
	
	/**
	 * List of contexts
	 */
	private List<GrammarContext> context;
	
	/**
	 * All noise words
	 */
	private Map<String, Set<String>> noise;
	
	/**
	 * Aggregation of all previous data
	 * including the final output
	 */
	private Map<String, String> grammar;
	
	/**
	 * List of all localization
	 */
	private GrammarLocalization localization;
	
	/**
	 * Get the keyword
	 * @return keyword
	 */
	public String getKeyword() {
		return keyword;
	}
	
	/**
	 * Set the keyword 
	 * @param keyword
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword.toLowerCase();
	}
	
	/**
	 * List all words that are in the different entries
	 * @return
	 */
	public Collection<String> getAllWords(){
		List<String> words = new ArrayList<String>();
		words.add(this.keyword);
		words.addAll(this.data.values().stream().flatMap(x -> x.stream()).collect(Collectors.toList()));
		words.addAll(this.actions.values().stream().flatMap(x -> x.stream()).collect(Collectors.toList()));
		words.addAll(this.noise.values().stream().flatMap(x -> x.stream()).collect(Collectors.toList()));
		words.addAll(this.localization.getLocalizations().values().stream().flatMap(x -> x.stream()).collect(Collectors.toList()));
		words.addAll(this.localization.getStarters());
		words.addAll(this.context.stream().map(c -> c.getWords()).flatMap(x -> x.stream()).collect(Collectors.toList()));
		return words;
	}

	/**
	 * Return the list of actions
	 * @return
	 */
	public Map<String, Collection<String>> getActions() {
		return Collections.unmodifiableMap(actions);
	}
	
	/**
	 * Return the list of all action words
	 * @return
	 */
	public List<String> getActionWords(){
		return this.actions.entrySet()
			.stream().map(e -> e.getValue())
			.flatMap(x -> x.stream())
			.collect(Collectors.toList());
	}
	
	/**
	 * Add a new action value
	 * @param action Action entry
	 * @param value Action value
	 */
	public void addAction(String action, String value) {
		if(!this.actions.containsKey(action)) {
			this.actions.put(action, new HashSet<>());
		}
		this.actions.get(action).add(value.toLowerCase());
	}
	
	/**
	 * Remove an action by its name & value
	 * If it's the last value of the entry, remove the entry
	 * @param action Action entry
	 * @param value Action value
	 */
	public void removeAction(String action, String value) {
		if(this.actions.containsKey(action)) {
			this.actions.get(action).remove(value);
			if(this.actions.get(action).isEmpty()) {
				this.actions.remove(action);
			}
		}
	}

	/**
	 * Get the list of data
	 * @return
	 */
	public Map<String, Collection<String>> getData() {
		return Collections.unmodifiableMap(data);
	}
	
	/**
	 * Add a new data value
	 * @param entry Data entry name
	 * @param value Data value
	 */
	public void addData(String entry, String value) {
		if(!this.data.containsKey(entry)) {
			this.data.put(entry, new HashSet<>());
		}
		this.data.get(entry).add(value.toLowerCase());
	}
	
	/**
	 * Remove a value by its name & value
	 * If it's the last value of the entry, remove the entry
	 * @param entry Data entry
	 * @param value Data value
	 */
	public void removeData(String entry, String value) {
		if(this.data.containsKey(entry)) {
			this.data.get(entry).remove(value);
			if(this.data.get(entry).isEmpty()) {
				this.data.remove(entry);
			}
		}
	}
	
	/**
	 * Get the list of noise
	 * @return
	 */
	public Map<String, Collection<String>> getNoise() {
		return Collections.unmodifiableMap(noise);
	}
	
	/**
	 * List of all noise words
	 * @return
	 */
	public List<String> getNoiseWords(){
		return this.noise.entrySet()
				.stream().map(e -> e.getValue())
				.flatMap(x -> x.stream())
				.collect(Collectors.toList());
	}
	
	/**
	 * Add a noise word
	 * @param entry Noise entry
	 * @param word Noise value
	 */
	public void addNoise(String entry, String word) {
		if(!this.noise.containsKey(entry)) {
			this.noise.put(entry, new HashSet<>());
		}
		this.noise.get(entry).add(word.toLowerCase());
	}
	
	/**
	 * Remove an noise by its name & value
	 * If it's the last value of the entry, remove the entry
	 * @param action Noise entry
	 * @param value Noise value
	 */
	public void removeNoise(String entry, String word) {
		if(this.noise.containsKey(entry)) {
			this.noise.get(entry).remove(word);
			if(this.noise.get(entry).isEmpty()) {
				this.noise.remove(entry);
			}
		}
	}

	/**
	 * Get the list of grammar output
	 * @return
	 */
	public Map<String, String> getGrammar() {
		return Collections.unmodifiableMap(this.grammar);
	}
	
	/**
	 * Add a new grammar definition
	 * Can't update the "output" entry
	 * @param definition Definition name
	 * @param value Definition value
	 */
	public void addGrammar(String definition, String value) {
		if(definition.equals(GRAMMAR_OUPUT)) {
			throw new IllegalArgumentException("Can't update output from grammar definition");
		}
		this.grammar.put(definition, value);
	}
	
	/**
	 * Remove a grammar definition by its name
	 * Can't remove the "output" entry
	 * @param definition Definition name
	 */
	public void removeGrammar(String definition) {
		if(definition.equals(GRAMMAR_OUPUT)) {
			throw new IllegalArgumentException("Can't remove output from grammar definition");
		}
		this.grammar.remove(definition);
	}
	
	/**
	 * Update the output grammar definition
	 * @param value
	 */
	public void updateOutputDefinition(String value) {
		this.grammar.put(GRAMMAR_OUPUT, value);
	}
	
	/**
	 * Get localization object
	 * @return
	 */
	public GrammarLocalization getLocalization() {
		return this.localization;
	}
	
	/**
	 * List of context
	 * @return
	 */
	public List<GrammarContext> getContext() {
		return Collections.unmodifiableList(context);
	}
	
	/**
	 * List of all context words
	 * @return
	 */
	public List<String> getContextWords(){
		return this.context.stream().map(e -> e.getWords())
				.flatMap(x -> x.stream())
				.collect(Collectors.toList());
	}
	
	/**
	 * Add or update a new context to the list
	 * @param context
	 */
	public void addContext(GrammarContext context) {
		Optional<GrammarContext> existingContext = this.context.stream().filter(c -> c.getType().equals(context.getType())).findFirst();
		if(existingContext.isPresent()) {
			existingContext.get().updateContext(context);
		} else {
			// build a new context based on the one to add
			// in order to set words to lowercase
			this.context.add(
				new GrammarContext(context.getType(), context.getWords().stream().map(String::toLowerCase).collect(Collectors.toSet()), context.getActions())
			);
		}
	}
	
	/**
	 * Remove a context by its name
	 * @param context
	 */
	public void removeContext(String context) {
		this.context = this.context.stream().filter(c -> !c.getType().equals(context)).collect(Collectors.toList());
	}
	
	/**
	 * Find all actions entry by a word value
	 * @param word Word value
	 * @return Action entry name if found, null else
	 */
	public Map<String, List<String>> findActionsTypeByWord(String word) {
		return this.actions.entrySet()
				.stream()
				.map(e -> {
					return new AbstractMap.SimpleEntry<String, List<String>>(e.getKey(), e.getValue().stream().filter(actionWord -> actionWord.startsWith(word)).collect(Collectors.toList()));
				})
				.filter(e -> e.getValue().size() > 0)
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}
	
	/**
	 * Find a context by word value and action type name
	 * @param word Context word
	 * @param action Context action
	 * @return
	 */
	public GrammarContext findContextByWordAndAction(String word, String action) {
		Optional<GrammarContext> context = this.context
				.stream()
				.filter(e -> e.getWords().contains(word) && e.containsAction(action))
				.findFirst();
		if(context.isPresent()) {
			return context.get();
		}
		return null;
	}
	
	/**
	 * Find the list of context that match a data word and action type name
	 * @param word Data word
	 * @param action Action type 
	 * @return
	 */
	public List<GrammarContext> findContextByDataWordAndAction(String word, String action){
		return this.context
				.stream()
				.filter(e -> e.containsDataTypeForAction(word, action))
				.collect(Collectors.toList());
	}
	
	/**
	 * Filter the list of data values to match the given word
	 * and return the data type name of found values
	 * @param word
	 * @return
	 */
	public List<String> findDataType(String word) {
		return this.data.entrySet()
				.stream()
				.filter(e -> e.getValue().contains(word))
				.map(e -> e.getKey())
				.collect(Collectors.toList());
	}
	
	@Override
	public String toString() {
		return "Grammar [keyword=" + keyword + ", data=" + data + ", actions=" + actions + ", context=" + context
				+ ", noise=" + noise + ", grammar=" + grammar + "]";
	}
}
