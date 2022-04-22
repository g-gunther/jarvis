package com.gguproject.jarvis.core.events.support;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventSender;

public abstract class SocketEvent implements DistributedEvent {
	private static final long serialVersionUID = -7200460036546571708L;

	/** Time when the event was generated. */
	private final long timestamp;
	
	/** UUID of the JVM that created the event. */
	private final UUID uuid;

	/** Object value. */
	private final String data;
	
	/** Event sender */
	private EventSender eventSender;
	
	protected SocketEvent(UUID uuid, EventSender eventSender, long timestamp, String data) {
		this.uuid = uuid;
		this.timestamp = timestamp;
		this.data = data;
		this.eventSender = eventSender;
	}
	
	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String getData() {
		return data;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}
	
	@Override
	public EventSender getEventSender() {
		return this.eventSender;
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this)
			.append("timestamp", this.timestamp)
			.append("uuid", this.uuid)
			.append("data", this.data)
			.append("eventSender", this.eventSender)
			.toString();
	}
}
