package com.gguproject.jarvis.core.events.multicast.support;

import java.util.UUID;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventSender;
import com.gguproject.jarvis.core.events.support.SocketEvent;

/**
 * Distributed event that can be send to / received from a datagram socket.
 */
public abstract class MulticastEvent extends SocketEvent implements DistributedEvent {

	private static final long serialVersionUID = -5161725273052455499L;

	/**
	 * Magic number in the packet header. This is the ASCII value for "demp", which stands for
	 * "Distributed Events Management Protocol".
	 */
	protected static final int MAGIC = 0x64656d70;

	/**
	 * Current version of the datagram packet protocol. Versions below are (will be) supported, but
	 * if a datagram packet contains a version number greater than this, an exception is thrown.
	 */
	protected static final byte VERSION = 2;
	
	protected MulticastEvent(UUID uuid, EventSender eventSender, long timestamp, String data) {
		super(uuid, eventSender, timestamp, data);
	}
}
