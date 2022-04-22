package com.gguproject.jarvis.master.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.data.ExternalEventData;
import com.gguproject.jarvis.core.events.api.multicast.MulticastManager;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.command.event.ExternalCommandResponseEventData;

import javax.inject.Named;

/**
 * Common listener used to communicate with other node
 * It listens for {@link ExternalEventData} events and transmit the content
 * to the {@link MulticastManager}
 *
 * @author GGUNTHER
 */
@Named
public class ExternalCommandResponseEventListener extends AbstractEventListener<ExternalCommandResponseEventData> {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(ExternalCommandResponseEventListener.class);

    public ExternalCommandResponseEventListener(EventBusService eventBusService) {
        super(ExternalEventData.eventType, ExternalCommandResponseEventData.class, eventBusService);
    }

    @Override
    public void onEvent(DistributedEvent event, ExternalCommandResponseEventData data) {
        LOGGER.info("----------------");
        LOGGER.info("Command: {}", data.getCommand());
        data.getResponses().stream().filter(o -> !o.getLines().isEmpty()).forEach(r -> {
            LOGGER.info("- {}:{}", r.getPluginName(), r.getPluginVersion());
            r.getLines().forEach(LOGGER::info);
        });
        LOGGER.info("----------------");
    }
}
