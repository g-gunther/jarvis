package com.gguproject.jarvis.plugin.androidtv.connection;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.androidtv.event.AndroidTvAskForPinEventData;
import com.google.polo.pairing.PairingContext;
import com.google.polo.pairing.PairingListener;
import com.google.polo.pairing.PairingSession;

import javax.inject.Named;

/**
 * Listens for events sent during the pairing session. pairing listener
 */
@Named
public class PairingListenerImpl implements PairingListener {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(PairingListenerImpl.class);

	private final KeyStoreManager keystoreManager;

	private final EventBusService eventBusService;
	
	private PairingContext pairingContext;
	
	private PairingSession pairingSession;

	public PairingListenerImpl(KeyStoreManager keystoreManager, EventBusService eventBusService){
		this.keystoreManager = keystoreManager;
		this.eventBusService = eventBusService;
	}

	public void onPerformInputDeviceRole(PairingSession session) {
		this.pairingSession = session;
		this.eventBusService.externalEmit(new AndroidTvAskForPinEventData());
	}
	
	public void enterSecret(String secret) {
		LOGGER.debug("Try to pair with secret: " + secret);
		if (secret != null && secret.length() > 0) {
			try {
				byte[] secretBytes = this.pairingSession.getEncoder().decodeToBytes(secret);
				this.pairingSession.setSecret(secretBytes);
				if(this.pairingSession.hasSucceeded()) {
					LOGGER.info("Pairing successed, add certificate to keystore");
					this.keystoreManager.storeCertificate(this.pairingContext.getServerCertificate());
				}
			} catch (IllegalArgumentException exception) {
				LOGGER.error("Exception while decoding secret: ", exception);
				this.pairingSession.teardown();
			} catch (IllegalStateException exception) {
				// ISE may be thrown when session is currently terminating
				LOGGER.error("Exception while setting secret: ", exception);
				this.pairingSession.teardown();
			}
		} else {
			this.pairingSession.teardown();
		}
	}

	public void onLogMessage(LogLevel level, String message) {
		LOGGER.debug("Log: " + message + " (" + level + ")");
	}
	
	public void setPairingContext(PairingContext context) {
		this.pairingContext = context;
	}

	@Override
	public void onSessionEnded(PairingSession session) {
		// nothing
	}

	@Override
	public void onSessionCreated(PairingSession session) {
		// nothing
	}

	@Override
	public void onPerformOutputDeviceRole(PairingSession session, byte[] gamma) {
		// nothing
	}
}
