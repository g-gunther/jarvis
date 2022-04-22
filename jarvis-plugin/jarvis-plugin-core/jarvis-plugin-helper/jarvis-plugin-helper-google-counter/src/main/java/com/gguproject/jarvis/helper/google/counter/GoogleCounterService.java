package com.gguproject.jarvis.helper.google.counter;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class is used to stores & check counters
 * This can be used to check that we don't use a service more than a given number of time on a period 
 * @author guillaumegunther
 */
public abstract class GoogleCounterService<T extends Enum<T>> implements OnPostConstruct {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(GoogleCounterService.class);
	
	private Gson gsonParser;
	
	private Map<String, Counter> counters = new HashMap<>();
	
	public GoogleCounterService() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		gsonBuilder.registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
			@Override
			public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				return LocalDate.parse(json.getAsString());
			}
		});
		gsonBuilder.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {

			@Override
			public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
				return new JsonPrimitive(src.toString());
			}
		});
		
		this.gsonParser = gsonBuilder.enableComplexMapKeySerialization().setPrettyPrinting().disableHtmlEscaping().create();
	}
	
	@Override
	public void postConstruct() {
		File counterFile = this.getCounterFile();
		JsonReader reader;
		try {
			reader = new JsonReader(new FileReader(counterFile));
			Type type = new TypeToken<Map<String, Counter>>(){}.getType();
			this.counters = this.gsonParser.fromJson(reader, type);
		} catch (FileNotFoundException e) {
			LOGGER.error("Can't find the given counter file: {}", counterFile.getName(), e);
			throw new IllegalStateException("Not able to read the counter file " + counterFile.getAbsolutePath()) ;
		}
	}
	
	/**
	 * Return the file containing configuration of counters
	 * @return
	 */
	protected abstract File getCounterFile();
	
	/**
	 * Increment the given counter by 1 for the today date
	 * @param counterName
	 * @throws GoogleCounterException
	 */
	public void increment(T counterName) throws GoogleCounterException {
		this.increment(counterName, 1);
	}
	
	/**
	 * Increment the given counter for the today date
	 * @param counterName
	 * @throws GoogleCounterException
	 */
	public void increment(T counterName, int value) throws GoogleCounterException {
		Counter counter = this.getAndCheckCounter(counterName);
		counter.incrementToday(value);
		this.writeFile();
	}
	
	/**
	 * Check that the counter has reached his limit yet
	 * @param counterName
	 * @throws GoogleCounterException
	 */
	public void check(T counterName) throws GoogleCounterException {
		this.check(counterName, 1);
	}
	
	/**
	 * Check that the counter is still valid after adding a given value
	 * @param counterName
	 * @param value
	 * @throws GoogleCounterException
	 */
	public void check(T counterName, int value) throws GoogleCounterException {
		Counter counter = this.getAndCheckCounter(counterName);
		if(counter.count + value >= counter.max) {
			throw new GoogleCounterException("The limit for counter: '" + counterName + "' will be reached by adding '+ value +'");
		}
	}
	
	/**
	 * Get a counter by its name and check that the limit has been reached yet
	 * @param counterName
	 * @return
	 * @throws GoogleCounterException
	 */
	private Counter getAndCheckCounter(T counterName) throws GoogleCounterException {
		Counter counter = Optional.ofNullable(this.counters.get(counterName.name()))
				.orElseThrow(() -> new IllegalStateException("Can't find counter configuration for: " + counterName));
		
		this.calculateAndCleanCounterValue(counter);
		
		if(counter.count >= counter.max) {
			throw new GoogleCounterException("The limit for counter: '" + counterName + "' has been reached");
		}
		
		return counter;
	}

	/**
	 * Calculate the sum of counter values for the configured period
	 * and remove unecessary stored counters
	 * @param counter
	 */
	private void calculateAndCleanCounterValue(Counter counter) {
		LocalDate lowerDateLimit = counter.period.getLowerLimit();
		counter.dateCounters = counter.dateCounters.entrySet().stream()
			.filter(e -> e.getKey().isAfter(lowerDateLimit) || e.getKey().isEqual(lowerDateLimit))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
		counter.count = counter.dateCounters.values().stream().collect(Collectors.summingInt(Integer::intValue));
	}
	
	/**
	 * Write the configuration file
	 * @param grammar
	 */
	private void writeFile() {
		File counterFile = this.getCounterFile();
		try (Writer writer = new FileWriter(counterFile)) {
			this.gsonParser.toJson(counters, writer);
		} catch (IOException e) {
			LOGGER.error("Can't write the counter file: {}", counterFile.getName(), e);
			throw new IllegalStateException("Not able to write the counter file " + counterFile.getAbsolutePath()) ;
		}
	}
	
	/**
	 * Period type
	 * @author guillaumegunther
	 */
	public enum PeriodType {
		MONTH,
		DAY;
		
		public LocalDate getLowerLimit() {
			switch(this) {
				case MONTH: 
					return LocalDate.now().minusMonths(1);
				case DAY: 
					return LocalDate.now().minusDays(1);
				default:
					return LocalDate.now();
			}
		}
	}
	
	/**
	 * Counter class
	 * @author guillaumegunther
	 */
	private static class Counter {
		private PeriodType period;
		private int max;
		private Map<LocalDate, Integer> dateCounters;
		private int count;
		
		public void incrementToday(int value) {
			LocalDate date = LocalDate.now();
			
			if(this.dateCounters.containsKey(date)) {
				this.dateCounters.put(date, this.dateCounters.get(date) + value);
			} else {
				this.dateCounters.put(date, value);
			}
			
			count += value;
		}
	}
}
