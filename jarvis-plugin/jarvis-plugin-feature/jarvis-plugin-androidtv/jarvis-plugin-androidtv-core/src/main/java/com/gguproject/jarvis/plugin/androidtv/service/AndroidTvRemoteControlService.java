package com.gguproject.jarvis.plugin.androidtv.service;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.androidtv.connection.ConnectionHandler;
import com.gguproject.jarvis.plugin.androidtv.encoder.Key.Code;
import com.gguproject.jarvis.plugin.androidtv.util.AndroidTvException;

import javax.inject.Named;

@Named
public class AndroidTvRemoteControlService {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(AndroidTvRemoteControlService.class);

	private final ConnectionHandler connectingHandler;

	public AndroidTvRemoteControlService(ConnectionHandler connectingHandler){
		this.connectingHandler = connectingHandler;
	}

	/**
	 * Send Anymote commands to the Google TV device
	 * @throws AndroidTvException 
	 */
	public void sendChannel(int channel) throws AndroidTvException {
		LOGGER.debug("Send channel: {}", channel);
		for(Code c : Code.parseChannel(channel)) {
			this.send(c);
		}
	}
	
	public void send(Code code) throws AndroidTvException {
		if(code == null) {
			throw new AndroidTvException("Code to process can't be null");
		}
		LOGGER.info("Send key code: {}", code);

		this.connectingHandler.process();
		
		// wait to be sure the connection is completed
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new AndroidTvException("Error while waiting before sending code to android tv");
		}
		
		this.connectingHandler.sendKeyPress(code);
		
		// wait to be sure the data has been sent
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new AndroidTvException("Error while waiting before disconnecting");
		}
		
		this.disconnect();
	}
	
	/**
	 * Cleanup
	 */
	public void disconnect() {
		this.connectingHandler.disconnect();
	}
}
