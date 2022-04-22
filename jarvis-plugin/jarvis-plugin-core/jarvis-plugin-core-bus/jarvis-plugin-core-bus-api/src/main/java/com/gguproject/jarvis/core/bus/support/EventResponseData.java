package com.gguproject.jarvis.core.bus.support;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Optional;


/**
 * Event response data envelop
 * Has to be extended with specific response properties
 */
public class EventResponseData implements Serializable {
    private static final long serialVersionUID = 4298317968309777241L;

    private static Gson gson = new Gson();

    private final boolean valuePresent;

    private final String data;

    public EventResponseData(Optional<?> response) {
        if(response.isPresent()) {
            this.valuePresent = true;
            this.data = gson.toJson(response.get());
        } else {
            this.valuePresent = false;
            this.data = null;
        }
    }

    public static EventResponseData empty(){
        return new EventResponseData(Optional.empty());
    }

    /**
     * Deserialize the response
     * @return
     */
    public <T> Optional<T> deserialize(Class<T> responseType) {
        return this.valuePresent ? Optional.of((T) gson.fromJson(this.data, responseType)) : Optional.empty();
    }
}
