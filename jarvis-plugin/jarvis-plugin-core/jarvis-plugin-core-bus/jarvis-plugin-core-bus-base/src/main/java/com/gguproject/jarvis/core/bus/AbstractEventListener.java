package com.gguproject.jarvis.core.bus;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventData;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.google.gson.Gson;

import java.util.Optional;

/**
 * Event listener
 *
 * @param <T> Type of event handled by this listener
 * @author guillaumegunther
 */
public abstract class AbstractEventListener<T extends EventData> implements EventListener<T> {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(AbstractEventListener.class);

    private static Gson gson = new Gson();

    private final String eventType;

    private final Class<T> eventClass;

    protected final EventBusService eventBusService;

    private String name;

    public AbstractEventListener(String eventType, Class<T> eventClass, EventBusService eventBusService) {
        this.eventType = eventType;
        this.eventClass = eventClass;
        this.eventBusService = eventBusService;
    }

    /**
     * Cast the event to the right type and process it
     *
     * @param event Event to process
     */
    @Override
    public Optional<Object> parseAndProcess(DistributedEvent event, String data) {
        return this.onEventWithResponse(event, this.deserialize(data));
    }

    /**
     * Name of the plugin/module this listeners is linked to
     *
     * @param name
     * @return
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getEventType() {
        return this.eventType;
    }

    @Override
    public T deserialize(String data) {
        return (T) gson.fromJson(data, this.eventClass);
    }

    /**
     * To override to process the event without returning data
     *
     * @param event Event to process
     */
    @Override
    public void onEvent(DistributedEvent event, T eventData) {
        LOGGER.error("onEvent has been called without being overridden for event: {}", event);
    }

    /**
     * To override to process the event and return data to the called
     *
     * @param event
     * @param eventData
     * @return
     */
    @Override
    public Optional<Object> onEventWithResponse(DistributedEvent event, T eventData) {
        this.onEvent(event, eventData);
        return Optional.empty();
    }
}
