package com.gguproject.jarvis.core.events.tcp.support;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventSender;
import com.gguproject.jarvis.core.events.support.SocketUtils;

public class TcpResponseEvent extends TcpEvent implements DistributedEvent {
	private static final long serialVersionUID = 1L;
	
	private TcpResponseEvent(UUID uuid, EventSender eventSender, long timestamp, String data) {
		super(uuid, eventSender, timestamp, data);
	}
	
	/**
	 * Build a Tcp response event based from the received packet
	 * @param senderHost
	 * @param packet
	 * @return
	 * @throws IOException 
	 */
	public static TcpResponseEvent buildResponseEvent(String senderHost, byte[] packet) throws IOException{
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet));
		
		byte[] bytes = new byte[15];
		dis.read(bytes);
		String senderIpAddress = SocketUtils.convertBytesToIpv4Address(bytes);
		
		int senderPort = dis.readInt();
		// Read timestamp
		long timestamp = dis.readLong();
		// Read UUID of originating JVM
		long most = dis.readLong();
		long least = dis.readLong();
		UUID uuid = new UUID(most, least);
		
		// Command
		String data = dis.readUTF();
		
		return new TcpResponseEvent(uuid, new EventSender(senderIpAddress, senderPort), timestamp, data);
	}
}
