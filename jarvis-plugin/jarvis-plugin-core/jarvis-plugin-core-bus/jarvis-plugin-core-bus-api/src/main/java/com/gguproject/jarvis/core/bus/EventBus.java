package com.gguproject.jarvis.core.bus;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventResponseData;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface of bus.
 * This bus is used to communicate between the app and all the plugins
 *
 * @author guillaumegunther
 */
public interface EventBus {

    /**
     * Register a listener
     *
     * @param eventListener Listener to register
     */
    public void registerListener(EventListener<?> eventListener);

    /**
     * Unregister listeners linked to a given name
     *
     * @param name
     */
    public void unregisterListenersByName(String name);

    /**
     * Emit a new event
     *
     * @param event Event to emit
     */
    public List<Future<EventResponseData>> emit(DistributedEvent event);
}
