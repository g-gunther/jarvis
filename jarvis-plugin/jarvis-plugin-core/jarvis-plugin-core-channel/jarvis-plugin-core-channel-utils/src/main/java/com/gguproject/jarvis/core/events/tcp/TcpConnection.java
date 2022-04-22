package com.gguproject.jarvis.core.events.tcp;


public class TcpConnection {
	private final String host;
	
	private final int port;
	
	public TcpConnection(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public String getHost(){
		return this.host;
	}
	
	public int getPort(){
		return this.port;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TcpConnection){
			TcpConnection c = (TcpConnection) obj;
			return c.getHost().equals(this.getHost()) && c.getPort() == this.getPort();
		}
		return false;
	}
	
	@Override
	public String toString(){
		return "Connection: " + this.host + ":" + this.port;
	}
}
