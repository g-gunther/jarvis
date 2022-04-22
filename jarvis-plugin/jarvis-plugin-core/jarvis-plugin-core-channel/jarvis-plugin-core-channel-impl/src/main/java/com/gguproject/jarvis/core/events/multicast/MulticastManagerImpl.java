package com.gguproject.jarvis.core.events.multicast;

import com.gguproject.jarvis.core.ApplicationConfiguration;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.EventData;
import com.gguproject.jarvis.core.events.api.multicast.MulticastManager;
import com.gguproject.jarvis.core.events.multicast.support.MulticastRequestEvent;
import com.gguproject.jarvis.core.events.multicast.support.MulticastResponseEvent;
import com.gguproject.jarvis.core.events.support.JVMUtils;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import javax.inject.Named;

/**
 * Events manager that propagates events to a multicast IP address.
 */
@Named
public class MulticastManagerImpl extends AbstractMulticastManager implements MulticastManager {
	/** Log */
	private static final Logger LOG = AbstractLoggerFactory.getLogger(MulticastManagerImpl.class);

	/** Receiver thread */
	private final MulticastReceiver receiver;

	private final EventBusService eventBusService;

	public MulticastManagerImpl(ApplicationConfiguration applicationConfiguration, MulticastReceiver receiver, EventBusService eventBusService){
		super(applicationConfiguration);
		this.receiver = receiver;
		this.eventBusService = eventBusService;
	}

	/**
	 * Initialize the multicast receiver
	 */
	@Override
	public void initialize() {
		this.receiver.setEventsManager(this);
		this.receiver.start();
	}

	/**
	 * Called in order to destroy the bean.
	 */
	@Override
	public void close() {
		super.close();

		// Stop the receiver thread
		if (this.receiver != null) {
			LOG.info("Stopping the multicast receiver");
			this.receiver.interrupt();
		}
	}

	@Override
	public void publish(EventData data) {
		this.publish(data.serialize());
	}
	
	@Override
	public void publish(String data) {
		LOG.debug("Send distributed event with data {}", data);

		// Create an event
		MulticastRequestEvent event = new MulticastRequestEvent(JVMUtils.getUUID(), this.getLocalEventSender(), data);

		// Send it
		this.sendEvent(event);

		// Dispatch in the local JVM
		this.eventBusService.emit(new MulticastResponseEvent(this.getLocalEventSender(), event));
	}
}
