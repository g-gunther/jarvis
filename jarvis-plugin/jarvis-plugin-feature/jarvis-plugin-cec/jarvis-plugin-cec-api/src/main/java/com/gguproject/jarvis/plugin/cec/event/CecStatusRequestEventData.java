package com.gguproject.jarvis.plugin.cec.event;

import com.gguproject.jarvis.core.bus.support.EventData;

/**
 * Command event data
 * @author guillaumegunther
 */
public class CecStatusRequestEventData extends EventData {
	private static final long serialVersionUID = -6841671964817624557L;
	public static final String eventType = "CEC_STATUS_REQUEST";
	
	public CecStatusRequestEventData() {
		super(eventType, CecStatusRequestEventData.class);
	}
}
