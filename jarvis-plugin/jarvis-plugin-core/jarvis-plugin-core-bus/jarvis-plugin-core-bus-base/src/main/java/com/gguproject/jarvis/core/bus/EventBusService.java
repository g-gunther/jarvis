package com.gguproject.jarvis.core.bus;

import com.gguproject.jarvis.core.bus.support.*;
import com.gguproject.jarvis.core.bus.support.data.ExternalEventData;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Wrapper for {@link EventBus}
 *
 * @author guillaumegunther
 */
@Named
public class EventBusService {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(EventBusService.class);

    /**
     * Event bus
     */
    private EventBus eventBus;

    /**
     * Name of the event bus service
     * will be set to all event listeners in order to know to which jar they belongs
     */
    private String name;

    /**
     * List of event listeners
     */
    private final List<AbstractEventListener<?>> eventListeners = new ArrayList<>();

    /**
     * Set the event bus service name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the event bus
     *
     * @return
     */
    public EventBus getEventBus() {
        return this.eventBus;
    }

    /**
     * Set the event bus
     *
     * @param eventBus Event bus
     */
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void registerListeners(List<AbstractEventListener> eventListeners) {
        eventListeners.forEach(eventListener -> {
            LOGGER.info("Register listener: {}", eventListener);
            this.eventListeners.add(eventListener);
            eventListener.setName(this.name);
            this.eventBus.registerListener(eventListener);
        });
    }

    /**
     * Unregister the listeners (remove them from the event bus)
     */
    public void unregisterListenersByName(String name) {
        this.eventBus.unregisterListenersByName(name);
    }

    /**
     * Emit locally data
     *
     * @param data
     */
    public void emit(EventData data) {
        this.emit(new LocalDistributedEvent(data.serialize()));
    }

    /**
     * Emit an event which will be sent externally
     * Check ExternalEventListener
     *
     * @param data
     */
    public void externalEmit(EventData data) {
        this.emit(new LocalDistributedEvent(new ExternalEventData(data).serialize()));
    }

    /**
     * Emit an event to a given external target
     * @param data
     * @param target
     */
    public void externalEmit(EventData data, EventSender target) {
        ExternalEventData externalEvent = new ExternalEventData(data);
        externalEvent.sentTo(target);
        this.emit(externalEvent);
    }

    /**
     * Emit an event to the bus
     *
     * @param event
     */
    public void emit(DistributedEvent event) {
        this.eventBus.emit(event);
    }

    /**
     * Emit locally data
     *
     * @param data
     */
    public <T> List<Optional<T>> emitAndWait(EventData data, Class<T> responseType) {
        return this.emitAndWait(new LocalDistributedEvent(data.serialize()), responseType);
    }

    /**
     * Emit an event to the bus and wait for its responses
     *
     * @param event
     * @return
     */
    public <T> List<Optional<T>> emitAndWait(DistributedEvent event, Class<T> responseType) {
        List<Future<EventResponseData>> futures = this.eventBus.emit(event);
        List<Optional<T>> responses = new ArrayList<>();
        futures.forEach(f -> {
            try {
                responses.add(f.get().deserialize(responseType));
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Not able to retreive response from bus processing", e);
            }
        });
        return responses;
    }
}
