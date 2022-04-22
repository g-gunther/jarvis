package com.gguproject.jarvis.core.events.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.gguproject.jarvis.core.events.tcp.TcpConnectionSender;

/**
 * Useful functions used to process sockets
 * @author GGUNTHER
 */
public class SocketUtils {

	/**
	 * Read all bytes from an input stream until the endPacket character is reached
	 * @param is Input stream to read
	 * @return List of bytes
	 * @throws IOException
	 */
	public static byte[] readBytesFromInputStream(InputStream is) throws IOException{
		byte[] buffer = new byte[1];
		List<Byte> receivedData = new ArrayList<Byte>();
		while(is.read(buffer) > 0){
			if(buffer[0] == TcpConnectionSender.endPacket){
				break;
			}
			receivedData.add(buffer[0]);
		}
		return ArrayUtils.toPrimitive(receivedData.toArray(new Byte[receivedData.size()]));
	}
	
	/**
	 * Convert an ipv4 address to a bytes array with a defined length of 15 caracters
	 * @param ipAddress Ip address to convert
	 * @return Bytes array
	 */
	public static byte[] convertIpv4AddressToBytes(String ipAddress) {
		// need to build an array of bytes with a defined length
		// retrieve all numbers of the ip address and set them on 3 digits if needed
		StringBuilder sb = new StringBuilder();
		for(String ipPart : ipAddress.split("\\.")) {
			if(sb.length() > 0) {
				sb.append(".");
			}
			sb.append(StringUtils.leftPad(ipPart, 3, "0"));
		}
		return sb.toString().getBytes();
	}
	
	/**
	 * Convert a bytes array to an ipv4 address
	 * @param bytes Bytes
	 * @return Ipv4 address
	 */
	public static String convertBytesToIpv4Address(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for(String ipPart : new String(bytes).split("\\.")) {
			if(sb.length() > 0) {
				sb.append(".");
			}
			sb.append(Integer.valueOf(ipPart).toString());
		}
		
		return sb.toString();
	}
}
