package com.gguproject.jarvis.plugin.display.event;

import com.gguproject.jarvis.core.bus.support.EventData;

/**
 * Command event data
 * @author guillaumegunther
 */
public class DisplayEventData extends EventData {
	private static final long serialVersionUID = -6841671964817624557L;
	private static final String eventType = "DISPLAY_EVENT";
	
	private String screenId;
	
	private Object data;
	
	public DisplayEventData(String screenId) {
		super(eventType, DisplayEventData.class);
		this.screenId = screenId;
	}
	
	public DisplayEventData(String screenId, Object data) {
		this(screenId);
		this.data = data;
	}
}
