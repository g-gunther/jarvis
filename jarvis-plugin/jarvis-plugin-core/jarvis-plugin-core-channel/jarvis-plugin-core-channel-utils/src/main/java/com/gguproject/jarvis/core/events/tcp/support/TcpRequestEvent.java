package com.gguproject.jarvis.core.events.tcp.support;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.gguproject.jarvis.core.bus.support.EventSender;
import com.gguproject.jarvis.core.events.support.SocketUtils;

public class TcpRequestEvent extends TcpEvent {
	private static final long serialVersionUID = 1L;
	
	public TcpRequestEvent(UUID uuid, EventSender eventSender, String data){
		super(uuid, eventSender, System.currentTimeMillis(), data);
	}
	
	/**
	 * Creates a datagram packet from the event's data.
	 * @param out Datagram output stream.
	 * @throws IOException If an I/O error occurs.
	 */
	public void writeTo(DataOutputStream dos) throws IOException {
		
		dos.write(SocketUtils.convertIpv4AddressToBytes(this.getEventSender().getIpAddress()));
		dos.writeInt(this.getEventSender().getPort());
		
		dos.writeLong(this.getTimestamp());
		dos.writeLong(this.getUUID().getMostSignificantBits());
		dos.writeLong(this.getUUID().getLeastSignificantBits());
		if (this.getData() == null) {
			dos.writeUTF("");
		} else {
			dos.writeUTF(this.getData());
		}
	}
}
