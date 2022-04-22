package com.gguproject.jarvis.plugin.speech.interpreter.grammar;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the localizations of the grammar
 */
public class GrammarLocalization {

	/**
	 * List of localizations
	 */
	private final Map<String, Set<GrammarWord>> values;

	public GrammarLocalization(Map<String, Set<GrammarWord>> values) {
		this.values = values;
	}
	
	/**
	 * Add a new localization value
	 * @param loc Localization entry
	 * @param value Localization value
	 */
//	public void addLocalization(String loc, String value) {
//		if(!this.values.containsKey(loc)) {
//			this.values.put(loc, new HashSet<>());
//		}
//		this.values.get(loc).add(GrammarWord.build(value.toLowerCase()));
//	}
	
	/**
	 * Find a localization by its words
	 * @param word
	 * @return
	 */
	public Optional<String> findLocalizationByWord(String word) {
		return this.values.entrySet()
				.stream()
				.filter(e -> {
					return e.getValue().stream()
						.filter(locWord -> locWord.match(word))
						.findFirst()
						.isPresent();
				})
				.map(e -> e.getKey())
				.findFirst();
	}

	@Override
	public String toString() {
		return "GrammarLocalization [localizations=" + values + "]";
	}
}
