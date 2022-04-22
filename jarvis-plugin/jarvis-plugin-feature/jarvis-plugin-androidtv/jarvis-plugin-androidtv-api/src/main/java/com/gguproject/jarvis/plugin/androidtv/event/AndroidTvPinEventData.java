package com.gguproject.jarvis.plugin.androidtv.event;

import com.gguproject.jarvis.core.bus.support.EventData;

public class AndroidTvPinEventData extends EventData{
	private static final long serialVersionUID = -6841671964817624557L;
	public static final String eventType = "ANDROIDTV_TVPIN";
	
	private String pin;
	
	public AndroidTvPinEventData() {
		super(eventType, AndroidTvPinEventData.class);
	}
	
	public AndroidTvPinEventData(String pin ) {
		this();
		this.pin = pin;
	}
	
	public String getPin() {
		return this.pin;
	}
}
