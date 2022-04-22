package com.gguproject.jarvis.plugin.speech.event;

import com.gguproject.jarvis.core.bus.support.EventData;

/**
 * Speech event data 
 */
public class SpeechEventData extends EventData {
	private static final long serialVersionUID = -8017535104899632667L;
	public static final String eventType = "SPEECH_EVENT";
	
	private String speech;
	
	public SpeechEventData() {
		super(eventType, SpeechEventData.class);
	}
	
	public SpeechEventData(String speech) {
		this();
		this.speech = speech;
	}
	
	public String getSpeech() {
		return this.speech;
	}

	@Override
	public String toString() {
		return "SpeechEventData [speech=" + speech + "]";
	}
}
