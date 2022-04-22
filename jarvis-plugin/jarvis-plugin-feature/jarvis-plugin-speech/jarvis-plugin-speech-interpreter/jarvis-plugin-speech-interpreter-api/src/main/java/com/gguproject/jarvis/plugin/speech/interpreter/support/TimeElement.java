package com.gguproject.jarvis.plugin.speech.interpreter.support;

/**
 * 
 * @author guillaumegunther
 *
 */
public class TimeElement {
	private TimeUnit unit;
	private int value;
	
	public TimeElement(TimeUnit unit, int value) {
		this.unit = unit;
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public TimeUnit getUnit() {
		return this.unit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		result = prime * result + value;
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
		TimeElement other = (TimeElement) obj;
		if (unit != other.unit)
			return false;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimeElement [unit=" + unit + ", value=" + value + "]";
	}
}
