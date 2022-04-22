package com.gguproject.jarvis.plugin.loader.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventSender;
import com.gguproject.jarvis.core.bus.support.LocalDistributedEvent;
import com.gguproject.jarvis.core.bus.support.data.ExternalEventData;
import com.gguproject.jarvis.core.events.api.multicast.MulticastManager;
import com.gguproject.jarvis.core.events.api.tcp.TcpManager;
import com.gguproject.jarvis.core.events.tcp.TcpConnection;
import com.gguproject.jarvis.core.events.tcp.support.TcpClientConnectionException;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import javax.inject.Named;

/**
 * Common listener used to communicate with other node
 * It listens for {@link ExternalEventData} events and transmit the content
 * to the {@link MulticastManager}
 *
 * @author GGUNTHER
 */
@Named
public class ExternalEventListener extends AbstractEventListener<ExternalEventData> {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(ExternalEventListener.class);


    private final MulticastManager multicastManager;

    private final TcpManager tcpManager;

    public ExternalEventListener(MulticastManager multicastManager, TcpManager tcpManager, EventBusService eventBusService) {
        super(ExternalEventData.eventType, ExternalEventData.class, eventBusService);
        this.multicastManager = multicastManager;
        this.tcpManager = tcpManager;
    }

    @Override
    public void onEvent(DistributedEvent event, ExternalEventData data) {
        LOGGER.debug("Event to broadcast to other nodes: {}", event);
        if (data.isBroadcastEvent()) {
            this.multicastManager.publish(data.getEventData());
        } else {
            try {
                if (EventSender.isLocal(data.getTargetIpAdress(), data.getTargetPort())) {
                    LOGGER.debug("The event to send is targeting the local app - emit it internally");
                    this.eventBusService.emit(new LocalDistributedEvent(data.getEventData()));
                } else {
                    this.tcpManager.sendToConnection(new TcpConnection(data.getTargetIpAdress(), data.getTargetPort()), data.getEventData());
                }
            } catch (TcpClientConnectionException e) {
                LOGGER.error("Not able to data", data, e);
            }
        }
    }
}
