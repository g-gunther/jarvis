package com.gguproject.jarvis.plugin.androidtv.event;

import com.gguproject.jarvis.core.bus.support.EventData;

public class AndroidTvAskForPinEventData extends EventData{
	private static final long serialVersionUID = -6841671964817624557L;
	private static final String eventType = "ANDROIDTV_ASK_FOR_PIN";
	
	public AndroidTvAskForPinEventData() {
		super(eventType, AndroidTvAskForPinEventData.class);
	}
}
