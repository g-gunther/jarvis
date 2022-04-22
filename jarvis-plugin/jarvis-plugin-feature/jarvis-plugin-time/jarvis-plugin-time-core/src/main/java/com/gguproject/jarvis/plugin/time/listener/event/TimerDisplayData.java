package com.gguproject.jarvis.plugin.time.listener.event;

import java.time.OffsetDateTime;

public class TimerDisplayData {

	private OffsetDateTime time;
	
	private TimerDisplayStatus status;
	
	public enum TimerDisplayStatus {
		SHOW,
		HIDE;
	}
	
	public static TimerDisplayData withTime(OffsetDateTime time) {
		TimerDisplayData data = new TimerDisplayData();
		data.time = time;
		data.status = TimerDisplayStatus.SHOW;
		return data;
	}
	
	public static TimerDisplayData withHideStatus() {
		TimerDisplayData data = new TimerDisplayData();
		data.status = TimerDisplayStatus.HIDE;
		return data;
	}

	public OffsetDateTime getTime() {
		return time;
	}

	public TimerDisplayStatus getStatus() {
		return status;
	}
}
