package com.gguproject.jarvis.plugin.freeboxv6.event.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.freeboxv6.event.event.InfraredCommandEventData;
import com.gguproject.jarvis.plugin.freeboxv6.event.service.InfraredService;

import javax.inject.Named;

/**
 * Service listener used to start the TV
 *
 * @author GGUNTHER
 */
@Named
public class CommandListener extends AbstractEventListener<InfraredCommandEventData> {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(CommandListener.class);

    private final InfraredService infraresService;

    public CommandListener(InfraredService infraresService, EventBusService eventBusService) {
        super(InfraredCommandEventData.eventType, InfraredCommandEventData.class, eventBusService);
        this.infraresService = infraresService;
    }

    @Override
    public void onEvent(DistributedEvent event, InfraredCommandEventData data) {
        this.infraresService.process(data.getCommands());
    }
}
