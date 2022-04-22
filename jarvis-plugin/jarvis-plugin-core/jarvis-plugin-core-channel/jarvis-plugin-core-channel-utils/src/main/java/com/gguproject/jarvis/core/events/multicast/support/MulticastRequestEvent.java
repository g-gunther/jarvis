package com.gguproject.jarvis.core.events.multicast.support;

import java.io.IOException;
import java.util.UUID;

import com.gguproject.jarvis.core.bus.support.EventSender;

/**
 * Distributed event that can be send to / received from a datagram socket.
 */
public class MulticastRequestEvent extends MulticastEvent {
	/** Serial version UID */
	private static final long serialVersionUID = 5693890405627520346L;
	
	/**
	 * Creates a new multicast event.
	 * @param uuid UUID of the JVM that creates the event.
	 * @param type Type of event.
	 * @param command Command.
	 */
	public MulticastRequestEvent(UUID uuid, EventSender eventSender, String data) {
		super(uuid, eventSender, System.currentTimeMillis(), data);
	}

	/**
	 * Creates a datagram packet from the event's data.
	 * @param out Datagram output stream.
	 * @throws IOException If an I/O error occurs.
	 */
	public void writeTo(DatagramOutputStream out) throws IOException {
		// Write the datagram
		out.writeInt(MAGIC);
		out.writeByte(VERSION);
		out.writeIpv4Address(this.getEventSender().getIpAddress());
		out.writeInt(this.getEventSender().getPort());
		out.writeLong(this.getTimestamp());
		out.writeUUID(this.getUUID());
		
		if (this.getData() == null) {
			out.writeString("");
		} else {
			out.writeString(this.getData());
		}
	}
}
