package com.gguproject.jarvis.master.channel;

import com.gguproject.jarvis.core.ApplicationConfiguration;
import com.gguproject.jarvis.core.bus.support.EventData;
import com.gguproject.jarvis.core.events.api.multicast.MulticastManager;
import com.gguproject.jarvis.core.events.multicast.AbstractMulticastManager;
import com.gguproject.jarvis.core.events.multicast.support.MulticastRequestEvent;
import com.gguproject.jarvis.core.events.support.JVMUtils;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import javax.inject.Named;

/**
 * Events manager that propagates events to a multicast IP address.
 */
@Named
public class MulticastManagerImpl extends AbstractMulticastManager implements MulticastManager {
	private static final Logger LOG = AbstractLoggerFactory.getLogger(MulticastManagerImpl.class);

	public MulticastManagerImpl(ApplicationConfiguration applicationConfiguration){
		super(applicationConfiguration);
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
	}
}
