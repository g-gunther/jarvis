package com.gguproject.jarvis.plugin.speech.interpreter.grammar;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents the grammar loaded from the speech configuration file
 */
public class Grammar {
	
	/**
	 * Specific french grammar values used for processing
	 */
	public static class GrammarFr {
		public static final List<String> spaceAndTimeDelimiter = Collections.unmodifiableList(Arrays.asList("dans", "de"));
		
		public static final List<String> exactLocalizationAndTimeDelimiter = Collections.unmodifiableList(Arrays.asList("à"));
		
		public static final List<String> noiseWords = Collections.unmodifiableList(Arrays.asList("le", "la", "les", "un", "une", "des", "d'", "l'"));
		
		public static final List<String> concatWords = Collections.unmodifiableList(Arrays.asList("et"));
		
		public static final List<String> allSmallWords = Stream.of(spaceAndTimeDelimiter, exactLocalizationAndTimeDelimiter, noiseWords, concatWords)
				.flatMap(x -> x.stream())
				.collect(Collectors.toUnmodifiableList());
		
		public static class Time {
			public static final String midday = "midi";
			public static final String midnight = "minuit";
			public static final String tomorrow = "demain";
			
			public static class HourModifier {
				public static final List<String> delimiter = Arrays.asList("de", "du");
				
				public static final String morning = "matin";
				public static final String afternoon = "l'après-midi";
				public static final String evening = "soir";
			}
		}
	}
	
	/**
	 * List of data values
	 */
	private Map<String, Set<GrammarWord>> data = new HashMap<>();
	
	/**
	 * List of all actions
	 */
	private Map<String, Set<GrammarWord>> actions;
	
	/**
	 * List of contexts
	 */
	private List<GrammarContext> contexts = new ArrayList<>();
	
	/**
	 * List of all localization
	 */
	private Map<String, Set<GrammarWord>> localizations;
	
	public boolean isSpaceAndTimeWordDelimiter(String word) {
		return GrammarFr.spaceAndTimeDelimiter.contains(word);
	}
	
	public boolean isNoiseWord(String word) {
		return GrammarFr.noiseWords.contains(word);
	}
	
	public boolean isConcatWord(String word) {
		return GrammarFr.concatWords.contains(word);
	}
	
	public boolean isActionWord(String word) {
		return this.actions.values()
			.stream()
			.flatMap(x -> x.stream())
			.filter(v -> v.match(word))
			.findAny()
			.isPresent();
	}
	
	/**
	 * Find all actions entry and their list of matching words by a word value
	 * @param words Word value
	 * @return Action entry name if found with its list of matching words, null else
	 */
	public Map<String, List<GrammarWord>> findActionsByWord(String words) {
		int nbWords = words.split(" ").length;
		
		// define a function that takes a filter as parameter
		// this will filter actions and find them by exact match first and then by sound match
		Function<Function<GrammarWord, Boolean>, Map<String, List<GrammarWord>>> actionFilterFunction = (checkFunction) -> {
			return this.actions.entrySet()
			.stream()
			.map(e -> {
				return new AbstractMap.SimpleEntry<String, List<GrammarWord>>(
					e.getKey(), 
					e.getValue().stream()
						.filter(actionWord -> {
							// there is more words in input than in action words -> leave
							if(actionWord.getWordLength() < nbWords) {
								return false;
							}
							return checkFunction.apply(actionWord);
						})
						.collect(Collectors.toList())
				);
			})
			.filter(e -> e.getValue().size() > 0)
			.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		};
		
		var result = actionFilterFunction.apply(actionWord -> actionWord.startsWith(words));
		
		if(result.isEmpty()) {
			result = actionFilterFunction.apply(actionWord -> actionWord.match(words, nbWords));	
		}
		
		return result;
	}
	
	/**
	 * Find a context by word value and action type name
	 * @param word Context word
	 * @param action Context action
	 * @return
	 */
	public GrammarContext findContextByWordAndAction(String word, String action) {
		
		Function<Function<GrammarWord, Boolean>, Optional<GrammarContext>> contextFilterFunction = (checkFunction) -> {
			return this.contexts
					.stream()
					.filter(e -> e.containsAction(action))
					.filter(e -> e.getWords().stream()
						.filter(contextWord -> checkFunction.apply(contextWord))
						.findFirst()
						.isPresent()
					)
					.findFirst();
		};
		
		
		var context = contextFilterFunction.apply(contextWord -> contextWord.exactMatch(word));
		
		if(context.isEmpty()) {
			context = contextFilterFunction.apply(contextWord -> contextWord.match(word));
		}
		
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
	public List<GrammarContext> findContextByDataWordAndAction(String dataType, String action){
		return this.contexts
				.stream()
				.filter(e -> e.containsDataTypeForAction(dataType, action))
				.collect(Collectors.toList());
	}
	
	/**
	 * Filter the list of data values to match the given word
	 * and return the data type name of found values
	 * @param word
	 * @return
	 */
	public Map<String, GrammarWord> findDataType(String word) {
		Map<String, GrammarWord> dataTypes = new HashMap<>();
		
		if(StringUtils.isEmpty(word)) {
			return dataTypes;
		}
		
		this.data.forEach((dataType, dataTypeWords) -> {
			dataTypeWords.stream()
				.filter(dataWord -> dataWord.exactMatch(word))
				.findFirst()
				.ifPresent(grammarword -> {
					dataTypes.put(dataType, grammarword);
				});
		});
		
		if(dataTypes.isEmpty()) {
			this.data.forEach((dataType, dataTypeWords) -> {
				dataTypeWords.stream()
					.filter(dataWord -> dataWord.match(word))
					.sorted((v1, v2) -> {
						return v1.distance(word) <= v2.distance(word) ? -1 : 1;
					})
					.findFirst()
					.ifPresent(grammarWord -> {
						dataTypes.put(dataType, grammarWord);
					});
			});
		}
		
		return dataTypes;
	}
	
	/**
	 * Find a localization by its words
	 * @param word
	 * @return
	 */
	public Optional<String> findLocalizationByWord(String word) {
		
		Function<Function<GrammarWord, Boolean>, Optional<String>> localizationFilterFunction = (checkFunction) -> {
			return this.localizations.entrySet()
					.stream()
					.filter(e -> e.getValue().stream()
								.filter(contextWord -> checkFunction.apply(contextWord))
								.findFirst()
								.isPresent()
					)
					.map(e -> e.getKey())
					.findFirst();
		};
		
		
		var localization = localizationFilterFunction.apply(contextWord -> contextWord.exactMatch(word));
		
		if(localization.isEmpty()) {
			localization = localizationFilterFunction.apply(contextWord -> contextWord.match(word));
		}
		
		return localization;
	}
	
	public List<String> getAllSmallWords(){
		return GrammarFr.allSmallWords;
	}
}
