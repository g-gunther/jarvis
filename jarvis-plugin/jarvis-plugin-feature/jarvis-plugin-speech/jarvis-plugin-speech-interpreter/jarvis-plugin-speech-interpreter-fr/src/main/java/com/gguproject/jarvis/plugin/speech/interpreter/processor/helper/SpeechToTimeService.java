package com.gguproject.jarvis.plugin.speech.interpreter.processor.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.gguproject.jarvis.plugin.speech.interpreter.grammar.Grammar.GrammarFr;
import com.gguproject.jarvis.plugin.speech.interpreter.support.TimeElement;
import com.gguproject.jarvis.plugin.speech.interpreter.support.TimeSpeechParserException;
import com.gguproject.jarvis.plugin.speech.interpreter.support.TimeUnit;

public class SpeechToTimeService {

	private static Map<String, Consumer<TimeElement>> hourModifiers = new HashMap<>();
	static {
		hourModifiers.put(GrammarFr.Time.HourModifier.morning, (timeElement) -> {});
		hourModifiers.put(GrammarFr.Time.HourModifier.afternoon, (timeElement) -> {
			if(timeElement.getValue() > 0 && timeElement.getValue() < 12) {
				timeElement.setValue(timeElement.getValue() + 12);
			}
		});
		hourModifiers.put(GrammarFr.Time.HourModifier.evening, (timeElement) -> {
			if(timeElement.getValue() > 0 && timeElement.getValue() < 12) {
				timeElement.setValue(timeElement.getValue() + 12);
			}
		});
	}
	
	private static Map<String, TimeElement> specialCases = new HashMap<>();
	static {
		specialCases.put(GrammarFr.Time.tomorrow, new TimeElement(TimeUnit.DAY, 1));
		specialCases.put(GrammarFr.Time.midday, new TimeElement(TimeUnit.HOUR, 12));
		specialCases.put(GrammarFr.Time.midnight, new TimeElement(TimeUnit.HOUR, 0));
	}
	
	private List<TimeElement> timeElements = new ArrayList<>();
	
	/**
	 * There is a numeric value with no unit
	 */
	private Integer pendingTimeValue = null;
	
	/**
	 * Set to true when a morning, afternoon, evening hour modifier is found
	 */
	private boolean hourModifierDelimiterFound = false;
	
	/**
	 * Sentence under analysis
	 */
	private String sentence;
	
	/**
	 * list of processors to apply to analyse the time speech
	 */
	private List<SpeechToTimeProcessor> processors = new ArrayList<>();
	
	public SpeechToTimeService() {
		
		/**
		 * if a concate word, move to the next part of the sentence
		 */
		processors.add(new SpeechToTimeProcessor() {
			public boolean canProcess(String value) {
				return GrammarFr.concatWords.contains(value);
			}
		});
		
		/**
		 * if delimiter found, it means the next word will be an night, afternoon or evening hour modifier
		 */
		processors.add(new SpeechToTimeProcessor() {
			@Override
			public boolean canProcess(String value) {
				return GrammarFr.Time.HourModifier.delimiter.contains(value);
			}
			
			public void process(String value) {
				hourModifierDelimiterFound = true;
			}
		});
		
		/**
		 * the previous word was a delimiter, it means this word is now a hour modifier
		 */
		processors.add(new SpeechToTimeProcessor() {
			@Override
			public boolean canProcess(String value) {
				return hourModifierDelimiterFound;
			}
			
			public void process(String value) throws TimeSpeechParserException {
				if(!hourModifiers.containsKey(value)) {
					throw new TimeSpeechParserException("No hour modifier found for value: '"+ value +"'");
				}

				TimeElement hourElement = timeElements.stream()
						.filter(timeElement -> timeElement.getUnit() == TimeUnit.HOUR)
						.findFirst()
						.orElseThrow(() -> new TimeSpeechParserException("No hour element found to update with modifier: '"+ value +"'"));
				
				hourModifiers.get(value).accept(hourElement);

				hourModifierDelimiterFound = false;
			}
		});
		
		/**
		 * special cases
		 */
		processors.add(new SpeechToTimeProcessor() {
			@Override
			public boolean canProcess(String value) {
				return specialCases.containsKey(value);
			}
			
			public void process(String value) {
				timeElements.add(specialCases.get(value));
			}
		});
		
		/**
		 * if the word contains digits & letters and not only digits
		 * it is a word having the following pattern: 2h or 2h30...
		 */
		processors.add(new SpeechToTimeProcessor() {
			@Override
			public boolean canProcess(String value) {
				return Pattern.matches("^([0-9]+[a-zA-Z]?)+$", value) && !Pattern.matches("^[0-9]+$", value);
			}
			
			public void process(String value) throws TimeSpeechParserException {
				// parse the value to extract numbers from the unit
				// example: 2h or 2h30 or 2h30m ...
				StringBuilder intSb = new StringBuilder();
				for(int c : value.toCharArray()) {
					// from 0 to 9
					if(c >= 48 && c <= 57) {
						intSb.append(c - 48);
					} else {
						if(intSb.length() == 0) {
							throw new TimeSpeechParserException("A time unit '"+ c +"' has been found but with no numeric value");
						}
						TimeUnit unit = TimeUnit.findByUnit(String.valueOf((char) c));
						timeElements.add(new TimeElement(unit, Integer.valueOf(intSb.toString())));
						intSb.setLength(0);
					}
				}
				
				if(intSb.length() > 0) {
					if(timeElements.size() == 0) {
						throw new TimeSpeechParserException("A numeric value '"+ intSb.toString() +"' has been found without time unit and there is no previous time elements");
					} else {
						TimeUnit unit = timeElements.get(timeElements.size() - 1).getUnit().findPrevious();
						timeElements.add(new TimeElement(unit, Integer.valueOf(intSb.toString())));
					}
				}
			}
		});
		
		/**
		 * If the words contains only digits, store it for later analysis
		 */
		processors.add(new SpeechToTimeProcessor() {
			@Override
			public boolean canProcess(String value) {
				return Pattern.matches("^[0-9]+$", value);
			}
			
			public void process(String value) throws TimeSpeechParserException {
				if(pendingTimeValue == null) {
					pendingTimeValue = Integer.valueOf(value);
				} else {
					throw new TimeSpeechParserException("Two consecutive numbers has been found without any units on speech: '"+ sentence +"'");
				}
			}
		});	
		
		/**
		 * Default cases, if pending value, should be a unit
		 */
		processors.add(new SpeechToTimeProcessor() {
			@Override
			public boolean canProcess(String value) {
				return true;
			}
			
			public void process(String value) throws TimeSpeechParserException {
				if(pendingTimeValue != null) {
					TimeUnit unit = TimeUnit.findByLabel(value);
					timeElements.add(new TimeElement(unit, pendingTimeValue));
					pendingTimeValue = null;
				} else {
					throw new TimeSpeechParserException("The value '"+ value +"' is not linked to any pending value");
				}
			}
		});	
	}
	
	/**
	 * The words should have the following format: 
	 * number unit (and number unit(optional)) like
	 * 2 hours and 5 minutes
	 * 2h (for 2 hours)
	 * 2h30 (for 2 hours and 30 minutes)
	 * 2m30 (for 2 minute and 30 secondes)
	 * 2 minutes 30
	 * 8h du matin vs 8h du soir (8 or 20)
	 * @param words
	 */
	public List<TimeElement> analyse(String sentence) throws TimeSpeechParserException {
		this.sentence = sentence;
		this.timeElements.clear();
		
		for(var sentencePart : sentence.split(" ")) {
			for(var processor : processors) {
				if(processor.canProcess(sentencePart)) {
					processor.process(sentencePart);
					break;
				}
			}
		}
		
		/*
		 * End of the speech, if there is still a pending numeric value, the unit is equals to the previous time elements found -1
		 */
		if(pendingTimeValue != null) {
			if(timeElements.isEmpty()) {
				throw new TimeSpeechParserException("The text '"+ sentence +"' contains only numbers, the unit can not be determined");
			} else {
				TimeUnit unit = timeElements.get(timeElements.size() - 1).getUnit().findPrevious();
				timeElements.add(new TimeElement(unit, pendingTimeValue));
			}
		} 
		
		return timeElements;
	}
	
	/**
	 * Interface of time processors
	 */
	public interface SpeechToTimeProcessor {
		/**
		 * Can the processor handle the given word?
		 * @param value
		 * @return
		 */
		public boolean canProcess(String value);
		
		/**
		 * Process the word
		 * @param value
		 * @throws TimeSpeechParserException
		 */
		public default void process(String value) throws TimeSpeechParserException {
			// do nothing
		};
	}
}
