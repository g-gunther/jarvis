package com.gguproject.jarvis.core.bus.support;

public class EventSender {

	public static final EventSender localEventSender = new EventSender("localhost", 0);
	
	private String ipAddress;
	
	private int port;
	
	public EventSender(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getPort() {
		return port;
	}
	
	public static boolean isLocal(String ipAddress, int port) {
		return localEventSender.getIpAddress().equals(ipAddress) && localEventSender.getPort() == port;
	}
}
