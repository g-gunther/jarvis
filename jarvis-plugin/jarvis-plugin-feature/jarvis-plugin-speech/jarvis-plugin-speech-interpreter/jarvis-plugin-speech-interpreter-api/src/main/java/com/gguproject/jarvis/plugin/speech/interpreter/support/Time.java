package com.gguproject.jarvis.plugin.speech.interpreter.support;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Time class which contains a list of time elements
 * which can then be converted to an {@link OffsetDateTime}
 * depending on if the elements represent and offset from now
 * or an exact time
 */
public class Time {

	/**
	 * Indicates if the given time elements are for an exact time or an offset from now
	 */
	private boolean exactTime = false;
	
	private List<TimeElement> elements = new ArrayList<>();
	
	public static Time build() {
		return new Time();
	}
	
	public Time add(TimeUnit unit, int value) {
		this.elements.add(new TimeElement(unit, value));
		return this;
	}
	
	public Time add(TimeElement element) {
		this.elements.add(element);
		return this;
	}
	
	public Time add(TimeElement...elements) {
		this.elements.addAll(Arrays.asList(elements));
		return this;
	}
	
	public Time add(List<TimeElement> elements) {
		this.elements.addAll(elements);
		return this;
	}
	
	public Time setExactTime() {
		this.exactTime = true;
		return this;
	}
	
	/**
	 * Check if a time has been defined or not
	 * @return
	 */
	public boolean isTimeDefined() {
		return !this.elements.isEmpty();
	}
	
	/**
	 * 
	 * @return
	 */
	public Collection<TimeElement> getTimeElements(){
		return Collections.unmodifiableCollection(this.elements);
	}
	
	public boolean isExactTime() {
		return this.exactTime;
	}
	
	/**
	 * Convert time elements to an {@link OffsetDateTime}
	 * @return
	 */
	public OffsetDateTime toOffsetDateTime() {
		OffsetDateTime targetTime = OffsetDateTime.now();
		if(this.elements.isEmpty()) {
			return targetTime;
		}
		
		if(this.exactTime) {
			int hour = elements.stream().filter(e -> e.getUnit() == TimeUnit.HOUR).map(e -> e.getValue()).findFirst().orElse(targetTime.getHour());
			int minute = elements.stream().filter(e -> e.getUnit() == TimeUnit.MINUTE).map(e -> e.getValue()).findFirst().orElse(0);
			int second = elements.stream().filter(e -> e.getUnit() == TimeUnit.SECOND).map(e -> e.getValue()).findFirst().orElse(0);
			
			targetTime = targetTime.withHour(hour)
				.withMinute(minute)
				.withSecond(second);
			
			// the target time is before, add 1 day for tomorrow
			if(targetTime.isBefore(OffsetDateTime.now())) {
				return targetTime.plusDays(1);
			}
			
			return targetTime;
		} else {
			int hour = elements.stream().filter(e -> e.getUnit() == TimeUnit.HOUR).map(e -> e.getValue()).findFirst().orElse(0);
			int minute = elements.stream().filter(e -> e.getUnit() == TimeUnit.MINUTE).map(e -> e.getValue()).findFirst().orElse(0);
			int second = elements.stream().filter(e -> e.getUnit() == TimeUnit.SECOND).map(e -> e.getValue()).findFirst().orElse(0);
			
			return targetTime.plusHours(hour)
				.plusMinutes(minute)
				.plusSeconds(second);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + (exactTime ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Time other = (Time) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		if (exactTime != other.exactTime)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Time [exactTime=" + exactTime + ", elements=" + elements + "]";
	}
}
