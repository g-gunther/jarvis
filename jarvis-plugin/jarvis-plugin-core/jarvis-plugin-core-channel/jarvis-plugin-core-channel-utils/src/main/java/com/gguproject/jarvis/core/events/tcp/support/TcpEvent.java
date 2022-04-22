package com.gguproject.jarvis.core.events.tcp.support;

import java.util.UUID;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventSender;
import com.gguproject.jarvis.core.events.support.SocketEvent;

public abstract class TcpEvent extends SocketEvent implements DistributedEvent {
	private static final long serialVersionUID = 1L;
	
	/** Flag indicating whether the command (String) is included in the datagram. */
	protected static final int FLG_COMMAND = 0x01;
	
	protected TcpEvent(UUID uuid, EventSender eventSender, long timestamp, String data) {
		super(uuid, eventSender, timestamp, data);
	}
}
