package com.gguproject.jarvis.core.events.api.tcp;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventData;
import com.gguproject.jarvis.core.events.tcp.TcpConnection;
import com.gguproject.jarvis.core.events.tcp.support.TcpClientConnectionException;

/**
 * Service used to send commands (context + action) to a specified nodes
 * used the TCP protocol 
 * @author GGUNTHER
 */
public interface TcpManager {
	
	public static String LOCAL_HOST = "localhost";

	/**
	 * Send a response to the node sending the received event
	 * @param receivedEvent Received event
	 * @param eventData Event data
	 * @throws TcpClientConnectionException 
	 */
	public void answerToReceivedEvent(DistributedEvent receivedEvent, EventData eventData) throws TcpClientConnectionException;
	
	/**
	 * 
	 * @param receivedEvent
	 * @param data
	 * @throws TcpClientConnectionException
	 */
	public void answerToReceivedEvent(DistributedEvent receivedEvent, String data) throws TcpClientConnectionException;
	
	/**
	 * Send a command to a given connection
	 * @param connection Connection
	 * @param context Context of the action
	 * @param action Action to perform
	 * @throws TcpClientConnectionException 
	 */
	public void sendToConnection(TcpConnection connection, EventData eventData) throws TcpClientConnectionException;
	
	/**
	 * Send a command to a given connection
	 * @param connection Connection
	 * @param context Context of the action
	 * @param action Action to perform
	 * @throws TcpClientConnectionException 
	 */
	public void sendToConnection(TcpConnection connection, String eventData) throws TcpClientConnectionException;
}
