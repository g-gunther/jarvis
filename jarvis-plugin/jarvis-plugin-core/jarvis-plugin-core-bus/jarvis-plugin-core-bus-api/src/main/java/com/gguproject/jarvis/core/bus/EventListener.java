package com.gguproject.jarvis.core.bus;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventData;

import java.util.Optional;

public interface EventListener<T extends EventData> {
    public Optional<Object> parseAndProcess(DistributedEvent event, String data);

    public void onEvent(DistributedEvent event, T eventData);

    public Optional<Object> onEventWithResponse(DistributedEvent event, T eventData);

    public T deserialize(String data);

    public String getEventType();

    public String getName();
}
