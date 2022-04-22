package com.gguproject.jarvis.plugin.cec.event;

import com.gguproject.jarvis.core.bus.support.EventData;

/**
 * Command event data
 * @author guillaumegunther
 */
public class CecStatusResponseEventData extends EventData {
	private static final long serialVersionUID = -6841671964817624557L;
	private static final String eventType = "CEC_STATUS_RESPONSE";
	
	private final PowerStatus powerStatus;
	
	private final boolean activeSource;
	
	public CecStatusResponseEventData(PowerStatus powerStatus, boolean activeSource) {
		super(eventType, CecStatusResponseEventData.class);
		this.powerStatus = powerStatus;
		this.activeSource = activeSource;
	}
	
	public PowerStatus getPowerStatus() {
		return powerStatus;
	}

	public boolean isActiveSource() {
		return activeSource;
	}

	@Override
	public String toString() {
		return "CecStatusResponseEventData [powerStatus=" + powerStatus + ", activeSource=" + activeSource + "]";
	}
}
