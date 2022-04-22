package com.gguproject.jarvis.core.events.support;

import com.gguproject.jarvis.core.ApplicationConfiguration;
import com.gguproject.jarvis.core.bus.support.EventSender;

import static com.gguproject.jarvis.core.ApplicationConfiguration.PropertyKey.tcpListenPort;

public abstract class AbstractSocketManager {

    protected ApplicationConfiguration applicationConfiguration;

    public AbstractSocketManager(ApplicationConfiguration applicationConfiguration){
        this.applicationConfiguration = applicationConfiguration;
    }

    protected EventSender getLocalEventSender() {
        return new EventSender(this.applicationConfiguration.getIpAddress(), this.applicationConfiguration.getIntProperty(tcpListenPort));
    }
}
