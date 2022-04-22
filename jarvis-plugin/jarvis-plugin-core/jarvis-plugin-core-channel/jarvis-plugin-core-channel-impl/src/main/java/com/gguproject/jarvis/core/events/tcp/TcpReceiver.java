package com.gguproject.jarvis.core.events.tcp;

import com.gguproject.jarvis.core.ApplicationConfiguration;
import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.events.tcp.support.TcpResponseEvent;
import com.gguproject.jarvis.core.ioc.context.annotation.Prototype;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import javax.inject.Named;

import static com.gguproject.jarvis.core.ApplicationConfiguration.PropertyKey.tcpListenPort;

/**
 * TCP receiver that could be launch in a separate thread
 * This received listens on a specific port defined in the configuration
 * and dispatch received events internally
 *
 * @author GGUNTHER
 */
@Named
@Prototype
public class TcpReceiver extends AbstractTcpReceiver implements OnPostConstruct {
    private static final Logger LOG = AbstractLoggerFactory.getLogger(TcpReceiver.class);

    private final EventBusService eventBusService;

    private final ApplicationConfiguration applicationConfiguration;

    public TcpReceiver(EventBusService eventBusService, ApplicationConfiguration applicationConfiguration) {
        super("SOCKET_RECEIVER");
        this.eventBusService = eventBusService;
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    public void postConstruct() {
        this.setPort(this.applicationConfiguration.getIntProperty(tcpListenPort));
    }

    protected void onMessage(TcpResponseEvent event) {
        eventBusService.emit(event);
    }
}
