package com.gguproject.jarvis.master.channel;

import com.gguproject.jarvis.core.ApplicationConfiguration;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventData;
import com.gguproject.jarvis.core.events.api.tcp.TcpManager;
import com.gguproject.jarvis.core.events.support.JVMUtils;
import com.gguproject.jarvis.core.events.tcp.AbstractTcpManagerImpl;
import com.gguproject.jarvis.core.events.tcp.TcpConnection;
import com.gguproject.jarvis.core.events.tcp.support.TcpRequestEvent;

import javax.inject.Named;

/**
 * Implementation of the {@link TcpManager} interface used to send data to a given connection
 * @author GGUNTHER
 */
@Named
public class TcpManagerImpl extends AbstractTcpManagerImpl implements TcpManager {

	public TcpManagerImpl(ApplicationConfiguration applicationConfiguration){
		super(applicationConfiguration);
	}

	@Override
	public void answerToReceivedEvent(DistributedEvent receivedEvent, EventData eventData) {
		TcpRequestEvent event = new TcpRequestEvent(JVMUtils.getUUID(), this.getLocalEventSender(), eventData.serialize());
		this.sendUsingConnection(new TcpConnection(receivedEvent.getEventSender().getIpAddress(), receivedEvent.getEventSender().getPort()), event);
	}
	
	@Override
	public void answerToReceivedEvent(DistributedEvent receivedEvent, String data) {
		TcpRequestEvent event = new TcpRequestEvent(JVMUtils.getUUID(), this.getLocalEventSender(), data);
		this.sendUsingConnection(new TcpConnection(receivedEvent.getEventSender().getIpAddress(), receivedEvent.getEventSender().getPort()), event);
	}
	
	@Override
	public void sendToConnection(TcpConnection connection, String eventData) {
		TcpRequestEvent event = new TcpRequestEvent(JVMUtils.getUUID(), this.getLocalEventSender(), eventData);
		this.sendUsingConnection(connection, event);
	}
	
	@Override
	public void sendToConnection(TcpConnection connection, EventData eventData) {
		TcpRequestEvent event = new TcpRequestEvent(JVMUtils.getUUID(), this.getLocalEventSender(), eventData.serialize());
		this.sendUsingConnection(connection, event);
	}
}
