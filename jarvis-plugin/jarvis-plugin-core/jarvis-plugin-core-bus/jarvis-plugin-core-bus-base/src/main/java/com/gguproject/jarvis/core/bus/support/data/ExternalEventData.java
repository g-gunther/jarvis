package com.gguproject.jarvis.core.bus.support.data;

import com.gguproject.jarvis.core.bus.support.EventData;
import com.gguproject.jarvis.core.bus.support.EventSender;

public class ExternalEventData extends EventData {
    private static final long serialVersionUID = 2534227526089433779L;
    public static final String eventType = "CORE_EXTERNAL_EVENT";
    private String eventData;

    private String targetIpAddress;

    private Integer targetPort;

    public ExternalEventData() {
        super(eventType, ExternalEventData.class);
    }

    public ExternalEventData(EventData eventData) {
        this();
        this.eventData = eventData.serialize();
    }

    public void sentTo(EventSender target) {
        this.targetIpAddress = target.getIpAddress();
        this.targetPort = target.getPort();
    }

    public boolean isBroadcastEvent() {
        return "".equals(this.targetIpAddress) || this.targetIpAddress == null || this.targetPort == null;
    }

    public String getEventData() {
        return this.eventData;
    }

    public String getTargetIpAdress() {
        return this.targetIpAddress;
    }

    public Integer getTargetPort() {
        return this.targetPort;
    }

    @Override
    public String toString() {
        return "ExternalEventData [eventData=" + eventData + ", targetIpAddress=" + targetIpAddress + ", targetPort="
                + targetPort + "]";
    }
}
