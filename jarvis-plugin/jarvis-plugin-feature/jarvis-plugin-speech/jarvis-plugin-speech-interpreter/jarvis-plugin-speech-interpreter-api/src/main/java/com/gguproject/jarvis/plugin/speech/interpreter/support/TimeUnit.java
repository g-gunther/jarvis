package com.gguproject.jarvis.plugin.speech.interpreter.support;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 
 * @author guillaumegunther
 *
 */
public enum TimeUnit {
	DAY(3, "d", "jours", "jour"),
	HOUR(2, "h", "heures", "heure"),
	MINUTE(1, "m", "minutes", "minute"),
	SECOND(0, "s", "secondes", "seconde");
	
	private int order;
	
	private String unit;
	
	private List<String> labels;
	
	private TimeUnit(int order, String unit, String...labels) {
		this.order = order;
		this.unit = unit;
		this.labels = Arrays.asList(labels);
	}
	
	public int getOrder() {
		return this.order;
	}
	
	public String getUnit() {
		return this.unit;
	}
	
	
	public TimeUnit findPrevious() throws TimeSpeechParserException {
		return Stream.of(values()).filter(u -> u.order < this.order)
			.sorted((u1, u2) -> Integer.compare(u2.order, u1.order))
			.findFirst()
			.orElseThrow(() -> new TimeSpeechParserException("Can't find previous unit of '"+ this.name() +"'"));
	}
	
	public static TimeUnit findByUnit(String unit) throws TimeSpeechParserException {
		for(TimeUnit u : values()) {
			if(u.getUnit().equals(unit)) {
				return u;
			}
		}
		throw new TimeSpeechParserException("Can't find time unit by unit value '"+ unit +"'");
	}
	
	public static TimeUnit findByLabel(String label) throws TimeSpeechParserException {
		for(TimeUnit u : values()) {
			if(u.labels.contains(label)) {
				return u;
			}
		}
		throw new TimeSpeechParserException("Can't find time unit by label '"+ label +"'");
	}
}
