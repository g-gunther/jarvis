package com.gguproject.jarvis.core.bus.support;

import java.util.UUID;


/**
 * Implementation of {@link DistributedEvent} to emit an event from a plugin or module
 *
 */
public class LocalDistributedEvent implements DistributedEvent {
	private static final long serialVersionUID = 7466585092788314243L;

	private long timestamp;
	
	private String data;
	
	public LocalDistributedEvent(String eventData) {
		this.data = eventData;
		this.timestamp = System.currentTimeMillis();
	}
	
	@Override
	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public String getData() {
		return this.data;
	}

	@Override
	public UUID getUUID() {
		return null;
	}

	@Override
	public EventSender getEventSender() {
		return EventSender.localEventSender;
	}

	@Override
	public String toString() {
		return "LocalDistributedEvent [timestamp=" + timestamp + ", data=" + data + "]";
	}
}
