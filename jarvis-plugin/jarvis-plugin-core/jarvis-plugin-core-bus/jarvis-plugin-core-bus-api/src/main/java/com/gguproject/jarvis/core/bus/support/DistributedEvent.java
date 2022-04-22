package com.gguproject.jarvis.core.bus.support;

import java.io.Serializable;
import java.util.UUID;

/**
 * Event envelop. It contains:
 * - the event data on its serialization form
 * - a unique identifier of the JVM which has sent it
 * - information of the sender
 */
public interface DistributedEvent extends Serializable {
	/** Datagram size, in bytes. */
	public static final int DEFAULT_DATAGRAM_SIZE = 32768;
	
	/**
     * Get the creation time of this event.
     * @return Time stamp.
     */
	public long getTimestamp();

	/**
     * Event data
     * @return
     */
	public String getData();

	/**
     * Get the unique identifier of the JVM where the event has been created.
     * @return UUID of the originating JVM.
     */
	public UUID getUUID();
	
	/**
	 * Get the sender information
	 * @return Sender data
	 */
	public EventSender getEventSender();
}
