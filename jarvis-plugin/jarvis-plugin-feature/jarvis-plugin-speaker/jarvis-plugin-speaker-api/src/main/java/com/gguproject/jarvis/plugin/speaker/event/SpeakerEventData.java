package com.gguproject.jarvis.plugin.speaker.event;

import com.gguproject.jarvis.core.bus.support.EventData;

/**
 * Speaker event data
 * @author guillaumegunther
 */
public class SpeakerEventData extends EventData {
	private static final long serialVersionUID = -6841671964817624557L;
	public static final String eventType = "SPEAKER_EVENT";
	
	public static SpeakerEventData withSpeech(String speech) {
		return new SpeakerEventData(speech, null);
	}
	
	public static SpeakerEventData withSpeechCode(String speechCode) {
		return new SpeakerEventData(null, speechCode);
	}
	
	/**
	 * Text
	 */
	private final String speech;
	
	/**
	 * 
	 */
	private final String speechCode;
	
	/**
	 * Constructor
	 * @param text Text
	 */
	private SpeakerEventData(String speech, String speechCode) {
		super(eventType, SpeakerEventData.class);
		this.speech = speech;
		this.speechCode = speechCode;
	}
	
	
	public String getSpeech() {
		return this.speech;
	}
	
	public String getSpeechCode() {
		return this.speechCode;
	}

	@Override
	public String toString() {
		return "SpeakerEventData [speech=" + speech + ", speechCode=" + speechCode + "]";
	}
}
