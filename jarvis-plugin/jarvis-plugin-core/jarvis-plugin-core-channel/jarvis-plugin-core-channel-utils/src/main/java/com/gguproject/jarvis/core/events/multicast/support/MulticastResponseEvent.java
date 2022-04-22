package com.gguproject.jarvis.core.events.multicast.support;

import java.io.IOException;
import java.util.UUID;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventSender;
import com.gguproject.jarvis.core.events.multicast.MulticastManagerStats;

public final class MulticastResponseEvent extends MulticastEvent implements DistributedEvent {
	private static final long serialVersionUID = 2664663145962717937L;

	/**
	 * Creates a new multicast event from an incoming datagram packet.
	 * @param in Input stream over a datagram packet.
	 * @param stats Global statistics, updated in case of errors.
	 * @throws IOException If an I/O error occurs or if the datagram packet is invalid.
	 */
	public static MulticastResponseEvent buildResponseEvent(String senderHost, DatagramInputStream in, MulticastManagerStats stats) throws IOException {
		// Check magic number
		int magic = in.readInt();
		if (magic != MAGIC) {
			throw new IOException("Bad magic number in datagram packet. Expected "
					+ Integer.toHexString(MAGIC) + ", got " + Integer.toHexString(magic)
					+ ". Packet is corrupted!");
		}

		// Check datagram packet version
		int version = in.readByte();
		if (version != VERSION) {
			stats.incReceivedBadPacket();
			throw new IOException("Bad version number in datagram packet. Expected " + VERSION
					+ ", got " + version + ". Packet is corrupted!");
		}
		
		String senderIpAddress = in.readIpv4Address();
		int senderPort = in.readInt();

		// Read timestamp
		long timestamp = in.readLong();

		// Read UUID of originating JVM
		UUID uuid = in.readUUID();

		// Command
		String data = in.readString();
		
		//return new MulticastResponseEvent(uuid, new EventSender(senderIpAddress, senderPort), timestamp, EventDataDeserializer.deserizalize(data));
		return new MulticastResponseEvent(uuid, new EventSender(senderIpAddress, senderPort), timestamp, data);
	}
	
	/**
	 * Create a new received event
	 * @param senderHost Address of the sender
	 * @param in Input data stream
	 * @param stats Stats object
	 * @throws IOException Exception
	 */
	private MulticastResponseEvent(UUID uuid, EventSender eventSender, long timestamp, String data) throws IOException {
		super(uuid, eventSender, timestamp, data);
	}
	
	/**
	 * Create a new received event based on a standard event
	 * @param senderHost Address of the sender
	 * @param event Original event
	 */
	public MulticastResponseEvent(EventSender eventSender, MulticastRequestEvent event){
		super(event.getUUID(), eventSender, event.getTimestamp(), event.getData());
	}
}
