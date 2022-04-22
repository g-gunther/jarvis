package com.gguproject.jarvis.core.events.multicast;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.daemon.DaemonThread;
import com.gguproject.jarvis.core.events.multicast.support.DatagramInputStream;
import com.gguproject.jarvis.core.events.multicast.support.MulticastResponseEvent;
import com.gguproject.jarvis.core.events.support.JVMUtils;
import com.gguproject.jarvis.core.ioc.context.annotation.Prototype;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.Hexadecimal;

import javax.inject.Named;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Daemon thread that waits for incoming datagram packets, parses them into events and dispatches those events
 * to the interested listeners.
 */
@Named
@Prototype
public final class MulticastReceiver extends DaemonThread {
    /**
     * Log
     */
    private static final Logger LOG = AbstractLoggerFactory.getLogger(MulticastReceiver.class);

    /**
     * Service that handles events execution.
     */
    private EventBusService eventBusService;

    /**
     * events manager
     */
    private AbstractMulticastManager eventsManager;

    /**
     * Receive buffer for the datagram.
     */
    private final byte[] receiveBuffer = new byte[DistributedEvent.DEFAULT_DATAGRAM_SIZE];

    /**
     * Receiver constructor
     */
    public MulticastReceiver(EventBusService eventBusService) {
        super("MULTICAST_RECEIVER");
        this.eventBusService = eventBusService;
    }

    public void setEventsManager(AbstractMulticastManager eventsManager) {
        this.eventsManager = eventsManager;
    }

    @Override
    public void run() {
        LOG.info("Ready to receive datagram packets. JVM UUID = {}", JVMUtils.getUUID());
        while (true) {
            try {
                run0();
            } catch (InterruptedException e) {
                LOG.error("Stop receiving multicast events (thread has been interrupted)", e);
                return;
            }
        }
    }

    /**
     * Wait for a datagram, parse it as an event and dispatch it to our listeners.
     *
     * @throws InterruptedException If the thread has been interrupted.
     */
    private void run0() throws InterruptedException {
        LOG.debug("Waiting for datagram packets...");

        // Receive a datagram packet
        DatagramPacket packet = new DatagramPacket(this.receiveBuffer, 0, this.receiveBuffer.length);
        try {
            this.eventsManager.getSocket().receive(packet);
        } catch (IOException e) {
            this.eventsManager.getStats().incReceiveFailures();
            LOG.error("I/O error while waiting for an event", e);
            if ("Socket closed".equalsIgnoreCase(e.getMessage())) {
                throw new InterruptedException(e.getMessage());
            }
            return;
        }

        // Update stats.
        this.eventsManager.getStats().incReceivedPackets();
        this.eventsManager.getStats().addReceivedSize(packet.getLength());

        if (LOG.isDebugEnabled()) {
            LOG.debug("Datagram packet received from multicast address {}:{} ({} bytes)",
                    packet.getAddress(), packet.getPort(), packet.getLength());
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Raw packet data: {}",
                    Hexadecimal.toString(packet.getData(), packet.getOffset(), packet.getLength()));
        }

        // Parse the packet data into an event
        MulticastResponseEvent event;
        try {
            try (DatagramInputStream in = new DatagramInputStream(packet, this.eventsManager.getStats())) {
                event = MulticastResponseEvent.buildResponseEvent(((InetSocketAddress) packet.getSocketAddress()).getHostName(), in, this.eventsManager.getStats());
                LOG.debug("Received event: {}", event);
            }
        } catch (IOException e) {
            // Could not decode the packet as an event. Dump everything to the log
            this.eventsManager.getStats().incReceivedBadEvent();
            LOG.error("Error while decoding the event", e);
            LOG.error("Datagram packet has been received from multicast address {}:{} ({} bytes)",
                    packet.getAddress(), packet.getPort(), packet.getLength());
            LOG.error("Raw packet data: {}",
                    Hexadecimal.toString(packet.getData(), packet.getOffset(), packet.getLength()));
            return;
        }

        /*
         * If the UUID of the event is the same as the UUID of the current JVM instance, we can silently
         * discard the event. It has been generated by the current JVM instance, so there is no need to
         * process it once again (it has been dispatched locally when sent)
         */
        UUID uuid = event.getUUID();
        if (JVMUtils.getUUID().equals(uuid)) {
            LOG.debug("Ignore incoming datagram packet, as it was created by our JVM (same UUID: {})",
                    uuid);
            return;
        }

        // Dispatch the event to all our local listeners
        //internalEventDispatcher.multicastEvent(new DispatcherEvent(event));
        eventBusService.emit(event);

        // Interrupted?
        if (isInterrupted()) {
            throw new InterruptedException();
        }
    }
}
