package com.gguproject.jarvis.core.bus;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventResponseData;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class EventBusMock implements EventBus {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(EventBusMock.class);

    @Override
    public void registerListener(EventListener<?> eventListener) {
    }

    @Override
    public void unregisterListenersByName(String name) {
    }

    @Override
    public List<Future<EventResponseData>> emit(DistributedEvent event) {
        LOGGER.debug("Emit event: {}", event);
        return new ArrayList<>();
    }
}
