package com.gguproject.jarvis.core.events.tcp;

import com.gguproject.jarvis.core.ApplicationConfiguration;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.events.api.tcp.TcpManager;
import com.gguproject.jarvis.core.events.support.AbstractSocketManager;
import com.gguproject.jarvis.core.events.tcp.support.TcpClientConnectionException;
import com.gguproject.jarvis.core.events.tcp.support.TcpRequestEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Implementation of the {@link TcpManager} interface used to send data to a given connection
 * @author GGUNTHER
 */
public abstract class AbstractTcpManagerImpl extends AbstractSocketManager{
	private static final Logger LOG = AbstractLoggerFactory.getLogger(AbstractTcpManagerImpl.class);

	/**
	 * Sender
	 */
	private TcpConnectionSender tcpConnectionSender = new TcpConnectionSender();

	public AbstractTcpManagerImpl(ApplicationConfiguration applicationConfiguration){
		super(applicationConfiguration);
	}

	/**
	 * Send an event request to the given connection
	 * @param connection Connection
	 * @param event Request event
	 * @throws TcpClientConnectionException 
	 */
	protected void sendUsingConnection(TcpConnection connection, TcpRequestEvent event) {
		try(
			ByteArrayOutputStream bos = new ByteArrayOutputStream(DistributedEvent.DEFAULT_DATAGRAM_SIZE); 
			DataOutputStream dos = new DataOutputStream(bos);
		){
			event.writeTo(dos);
			
			// Fail if the datagram is too big
			if (dos.size() > DistributedEvent.DEFAULT_DATAGRAM_SIZE) {
				LOG.warn("Cannot send datagram packet for event {} because it is too big ({} bytes), while the send buffer size is limited to {} bytes.",
						event, dos.size(), DistributedEvent.DEFAULT_DATAGRAM_SIZE);
				return;
			}
			
			byte[] tcpData = bos.toByteArray();
			
			this.tcpConnectionSender.send(connection, tcpData);
		} catch(IOException | TcpClientConnectionException e){
			LOG.error("Cannot send tcp packet", e);
		}
	}
}
