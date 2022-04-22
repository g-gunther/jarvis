package com.gguproject.jarvis.core.events.tcp.support;

public class TcpClientConnectionException extends Exception {
	private static final long serialVersionUID = 1512524983562243788L;

	public TcpClientConnectionException(String message, Throwable e){
		super(message, e);
	}
	
	public TcpClientConnectionException(String message){
		super(message);
	}
}
