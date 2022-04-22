package com.gguproject.jarvis.core.events.api.multicast;

import com.gguproject.jarvis.core.bus.support.EventData;

/**
 * Interface of event manager service.
 */
public interface MulticastManager {

    /** Name of the events manager bean in <code>applicationContext.xml</code> */
    String NAME = "eventsManager";

    /**
     * Publish a new event, which will be broadcasted to all the listeners that are active on the network.
     * @param eventData Event to publish
     */
    void publish(EventData data);
    
    void publish(String data);
    
}
