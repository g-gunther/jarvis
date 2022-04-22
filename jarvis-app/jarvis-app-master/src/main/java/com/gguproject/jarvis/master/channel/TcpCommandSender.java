package com.gguproject.jarvis.master.channel;

import com.gguproject.jarvis.core.ApplicationConfiguration;
import com.gguproject.jarvis.core.bus.support.EventData;
import com.gguproject.jarvis.core.events.support.JVMUtils;
import com.gguproject.jarvis.core.events.tcp.AbstractTcpManagerImpl;
import com.gguproject.jarvis.core.events.tcp.TcpConnection;
import com.gguproject.jarvis.core.events.tcp.support.TcpRequestEvent;

import javax.inject.Named;

@Named
public class TcpCommandSender extends AbstractTcpManagerImpl {

	public TcpCommandSender(ApplicationConfiguration applicationConfiguration){
		super(applicationConfiguration);
	}

	public void send(String ipAddress, int port, EventData data) {
		TcpRequestEvent event = new TcpRequestEvent(JVMUtils.getUUID(), this.getLocalEventSender(), data.serialize());
		this.sendUsingConnection(new TcpConnection(ipAddress, port), event);
	}
}
