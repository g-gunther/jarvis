package com.gguproject.jarvis.core.bus.support;

import com.google.gson.Gson;

import java.io.Serializable;


/**
 * Event data envelop
 * Has to be extended with specifi event properties
 */
public abstract class EventData implements Serializable {
    private static final long serialVersionUID = 4298317968309777241L;

    private static Gson gson = new Gson();

    private final transient String eventType;
    private final transient Class<?> eventClass;

    public EventData(String eventType, Class<?> eventClass) {
        this.eventType = eventType;
        this.eventClass = eventClass;
    }

    /**
     * Serialize the event with all its data
     * it also contains the event class name which will be used for deserialization
     *
     * @return
     */
    public String serialize() {
        return new StringBuilder(this.eventType)
                .append("@")
                .append(gson.toJson(this))
                .toString();
    }

    /**
     * Deserialize the event
     *
     * @param data
     * @return
     */
    public EventData deserialize(String data) {
        return (EventData) gson.fromJson(data, this.eventClass);
    }
}
