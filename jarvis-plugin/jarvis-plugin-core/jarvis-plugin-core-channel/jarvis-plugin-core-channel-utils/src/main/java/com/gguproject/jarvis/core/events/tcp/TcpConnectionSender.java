package com.gguproject.jarvis.core.events.tcp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.inject.Named;

import com.gguproject.jarvis.core.events.support.SocketUtils;
import com.gguproject.jarvis.core.events.tcp.support.TcpClientConnectionException;
import com.gguproject.jarvis.core.events.tcp.support.TcpStatus;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.StringBuilderUtils;

/**
 * Service used to send data over TCP
 * @author GGUNTHER
 */
@Named
public class TcpConnectionSender {
	private static final Logger LOG = AbstractLoggerFactory.getLogger(TcpConnectionSender.class);
	
	/** Special char that indicates the end of the packet */
	public final static byte endPacket = (byte) '\n';
	
	/**
	 * Send data over TCP
	 * @param connection Connection 
	 * @param data Data to send
	 * @return Status of the request
	 * @throws TcpClientConnectionException
	 */
	public TcpStatus send(TcpConnection connection, byte[] data) throws TcpClientConnectionException{
		LOG.debug("Send tcp message {} to {}:{}", data, connection.getHost(), connection.getPort());
		TcpStatus status = null;
		try {
			Socket socket = new Socket(connection.getHost(), connection.getPort());
			DataOutputStream request = new DataOutputStream(socket.getOutputStream());
			
			// send a message with a break line to identify the end of received data by the server
			request.write(data);
			request.write(endPacket);

			byte[] receivedData = SocketUtils.readBytesFromInputStream(socket.getInputStream());
			
			LOG.debug("Client tcp response status: {}", receivedData);
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(receivedData));
			status = this.checkServerResponse(dis.readInt());
			
			socket.close();
		} catch (IOException e) {
			throw new TcpClientConnectionException(
				StringBuilderUtils.build("An error occurs during the sending of a request to host: ")
					.append(connection.getHost())
					.append(" and port: ")
					.append(connection.getPort()).toString(),
			e);
		}
		
		return status;
	}
	
	/**
	 * Check the server response
	 * @param response
	 * @throws TcpClientConnectionException
	 */
	private TcpStatus checkServerResponse(int statusCode) throws TcpClientConnectionException {
		LOG.debug("Tcp server response: {}", statusCode);
		
		TcpStatus status = TcpStatus.findByCode(statusCode);
		if(status == null){
			throw new TcpClientConnectionException(
				StringBuilderUtils.build("Return status not found for server response: ")
					.append(statusCode)
					.toString()
			);
		}
		return status;
	}
}
