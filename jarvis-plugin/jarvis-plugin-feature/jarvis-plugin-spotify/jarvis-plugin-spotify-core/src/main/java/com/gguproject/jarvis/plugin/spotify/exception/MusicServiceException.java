package com.gguproject.jarvis.plugin.spotify.exception;

public class MusicServiceException extends Exception {
	private static final long serialVersionUID = -2306913362855451853L;

	public MusicServiceException(Throwable e){
		super(e);
	}
	
	public MusicServiceException(String message){
		super(message);
	}
}
