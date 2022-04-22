package com.gguproject.jarvis.plugin.androidtv.client;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.androidtv.connection.PairingListenerImpl;

import javax.inject.Named;

@Named
public class PinHandler {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(PinHandler.class);

	private final PairingListenerImpl pairingListener;

	public PinHandler(PairingListenerImpl pairingListener){
		this.pairingListener = pairingListener;
	}
	
	public void enterPin(String pin) {
		this.pairingListener.enterSecret(pin);
	}
}
