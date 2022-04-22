package com.gguproject.jarvis.core.events.tcp.support;

public enum TcpStatus {
	SUCCESS(200),
	SERVER_ERROR(500),
	UNTREATED(501);
	
	private int code;
	
	private TcpStatus(int code){
		this.code = code;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public static TcpStatus findByCode(int code){
		for(TcpStatus e : values()){
			if(e.getCode() == code){
				return e;
			}
		}
		return null;
	}
}
