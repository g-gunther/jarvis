package com.gguproject.jarvis.plugin.speech.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.plugin.freeboxv6.event.event.InfraredCommandEventData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command event listener.
 * It handles {@link InfraredCommandEventData} event types (context & action)
 *
 * @author guillaumegunther
 */
public abstract class AbstractCommandEventListener extends AbstractEventListener<InfraredCommandEventData> {

    /**
     * Context type
     */
    private final String context;

    /**
     * List of actions the listener can handle
     */
    private final List<String> actions = new ArrayList<>();

    /**
     * Constructor
     *
     * @param contextType Context type
     * @param actionTypes Actions
     */
    public AbstractCommandEventListener(EventBusService eventBusService, String context, String... actions) {
        super(InfraredCommandEventData.eventType, InfraredCommandEventData.class, eventBusService);
        this.context = context;
        this.actions.addAll(Arrays.asList(actions));
    }

    @Override
    public void onEvent(DistributedEvent event, InfraredCommandEventData data) {
        // check if the listener can handle the event
		/*if(this.context.equals(data.getContext()) 
				&& this.actions.contains(data.getAction())) {
			this.process(event, data);
		}*/
    }

    /**
     * Process the event
     *
     * @param event Event
     */
    public abstract void process(DistributedEvent event, InfraredCommandEventData data);
}
