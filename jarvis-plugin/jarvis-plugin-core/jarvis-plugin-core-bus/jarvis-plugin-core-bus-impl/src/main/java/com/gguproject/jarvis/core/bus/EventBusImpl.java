package com.gguproject.jarvis.core.bus;


import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventResponseData;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementation of the event bus
 */
public class EventBusImpl implements EventBus {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(EventBusImpl.class);

    /**
     * List of listeners categorized by event type
     */
    private Map<String, Set<EventListener<?>>> eventListeners = new HashMap<>();

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2, new ThreadFactory() {
        private AtomicInteger threadIndex = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "BUS-" + threadIndex.getAndIncrement());
        }
    });

    @Override
    public void registerListener(EventListener<?> eventListener) {
        LOGGER.debug("Register listener: {} with event type: {}", eventListener, eventListener.getEventType());

        if (!this.eventListeners.containsKey(eventListener.getEventType())) {
            this.eventListeners.put(eventListener.getEventType(), new HashSet<>());
        }

        this.eventListeners.get(eventListener.getEventType()).add(eventListener);
    }

    @Override
    public void unregisterListenersByName(String name) {
        LOGGER.debug("Unregister listeners for name: {}", name);

        List<EventListener<?>> listenersToRemove = this.eventListeners.values().stream()
                .flatMap(x -> x.stream())
                .filter(listener -> name.equals(listener.getName()))
                .collect(Collectors.toList());

        listenersToRemove.forEach(eventListener -> {
            if (this.eventListeners.get(eventListener.getEventType()).remove(eventListener)) {
                if (this.eventListeners.get(eventListener.getEventType()).isEmpty()) {
                    this.eventListeners.remove(eventListener.getEventType());
                }
            } else {
                LOGGER.error("Not able to remove listener: {}", eventListener);
            }
        });
    }

    @Override
    public List<Future<EventResponseData>> emit(DistributedEvent event) {
        String eventType = event.getData().substring(0, event.getData().indexOf('@'));
        String eventData = event.getData().substring(event.getData().indexOf('@') + 1);
        LOGGER.debug("Deserialize event from class: {} with data: {}", eventType, eventData);
        List<Future<EventResponseData>> futures = new ArrayList<>();

        if (this.eventListeners.containsKey(eventType)) {

            this.eventListeners.get(eventType)
                    .forEach(listener -> {
                        futures.add(executor.submit(() -> {
                            LOGGER.debug("Call listener {}", listener);
                            try {
                                return new EventResponseData(listener.parseAndProcess(event, eventData));
                            } catch (Throwable t) {
                                LOGGER.error("Error while processing listener {}", listener, t);
                            }
                            return EventResponseData.empty();
                        }));
                    });

        } else {
            LOGGER.info("Can't find any listeners for type {}", eventType);
        }

        return futures;
    }
}
