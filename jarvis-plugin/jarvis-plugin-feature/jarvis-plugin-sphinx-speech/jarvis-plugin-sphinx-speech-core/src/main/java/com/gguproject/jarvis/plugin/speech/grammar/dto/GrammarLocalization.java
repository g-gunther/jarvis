package com.gguproject.jarvis.plugin.speech.grammar.dto;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the localizations of the grammar
 */
public class GrammarLocalization {

	/**
	 * List of words that indicates there is a localization in the speech
	 */
	private List<String> starters;
	
	/**
	 * List of localizations
	 */
	private Map<String, Set<String>> values;

	/**
	 * Returns the list of starters
	 * @return
	 */
	public List<String> getStarters() {
		return Collections.unmodifiableList(starters);
	}

	/**
	 * Returns the list of localizations
	 * @return
	 */
	public Map<String, Set<String>> getLocalizations() {
		return Collections.unmodifiableMap(values);
	}
	
	/**
	 * Add a new localization value
	 * @param loc Localization entry
	 * @param value Localization value
	 */
	public void addLocalization(String loc, String value) {
		if(!this.values.containsKey(loc)) {
			this.values.put(loc, new HashSet<>());
		}
		this.values.get(loc).add(value.toLowerCase());
	}
	
	/**
	 * Remove a localization. If last value is removed, the localization entry is removed
	 * @param loc Localization entry to remove
	 * @param value Localization value to remove
	 */
	public void removeLocalization(String loc, String value) {
		if(this.values.containsKey(loc)) {
			this.values.get(loc).remove(value);
			if(this.values.get(loc).isEmpty()) {
				this.values.remove(loc);
			}
		}
	}
	
	/**
	 * Return the list of localization words
	 * @return
	 */
	public List<String> getLocalizationWords(){
		return this.values.entrySet().stream()
				.map(e -> e.getValue())
				.flatMap(e -> e.stream())
				.collect(Collectors.toList());
	}
	
	/**
	 * Find a localization by its words
	 * @param word
	 * @return
	 */
	public String findLocalizationByWord(String word) {
		Optional<String> localisation = this.values.entrySet()
				.stream()
				.filter(e -> e.getValue().contains(word))
				.map(e -> e.getKey())
				.findFirst();

		if(localisation.isPresent()) {
			return localisation.get();
		}
		return null;
	}

	@Override
	public String toString() {
		return "GrammarLocalization [starters=" + starters + ", localizations=" + values + "]";
	}
}
