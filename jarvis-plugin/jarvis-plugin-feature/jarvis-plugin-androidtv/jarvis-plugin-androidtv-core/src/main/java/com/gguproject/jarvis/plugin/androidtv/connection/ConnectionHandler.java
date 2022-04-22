/*
 * Copyright (C) 2012 Google Inc. All rights reserved.
 * Copyright (C) 2012 ENTERTAILION, LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gguproject.jarvis.plugin.androidtv.connection;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.androidtv.AndroidTvPluginConfiguration;
import com.gguproject.jarvis.plugin.androidtv.client.MessageSender;
import com.gguproject.jarvis.plugin.androidtv.client.MessageType;
import com.gguproject.jarvis.plugin.androidtv.encoder.Key.Code;
import com.gguproject.jarvis.plugin.androidtv.util.AndroidTvException;
import com.gguproject.jarvis.plugin.androidtv.util.Constants;
import com.gguproject.jarvis.plugin.androidtv.util.JavaPlatform;
import com.gguproject.jarvis.plugin.androidtv.util.Message;
import com.google.polo.exception.PoloException;
import com.google.polo.pairing.ClientPairingSession;
import com.google.polo.pairing.PairingContext;
import com.google.polo.pairing.message.EncodingOption;
import com.google.polo.ssl.DummySSLSocketFactory;
import com.google.polo.wire.PoloWireInterface;
import com.google.polo.wire.WireFormat;

import javax.inject.Named;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.*;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * This task covers entire connection mechanism, including pairing, when
 * necessary.
 */
@Named
public class ConnectionHandler implements OnPostConstruct {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(ConnectionHandler.class);

	private final AndroidTvPluginConfiguration configuration;

	private final KeyStoreManager keyStore;

	private final PairingListenerImpl pairingListener;

	private TvDevice target;
	
	private SSLSocket sslsock;

	private MessageSender messageSender;

	public ConnectionHandler(AndroidTvPluginConfiguration configuration, KeyStoreManager keyStore,
							 PairingListenerImpl pairingListener){
		this.configuration = configuration;
		this.keyStore = keyStore;
		this.pairingListener = pairingListener;
	}

	public void postConstruct() {
		String ipAddress = this.configuration.getProperty(AndroidTvPluginConfiguration.PropertyKey.ipAddress);
		int port = this.configuration.getIntProperty(AndroidTvPluginConfiguration.PropertyKey.port);
		try {
			Inet4Address address = (Inet4Address) InetAddress.getByName(ipAddress);
			this.target = new TvDevice(Constants.string.manual_ip_default_box_name, address, port);
		} catch (UnknownHostException e) {
			throw TechnicalException.get().message("Not able to resolve ip address of android tv: {0}", ipAddress).exception(e).build();
		}
	}
	
	/**
	 * Initialize background connection; notify the listener about results.
	 * @throws AndroidTvException 
	 */
	public void process() throws AndroidTvException {
		LOGGER.debug("Process connection");
		
		if(this.connect()) {
			try {
				boolean state = this.instantiateMessageSender(this.sslsock);
				if (state) {
					LOGGER.info("Connection to androidtv successed");
				} else {
					LOGGER.error("Connection to androidtv failed");
				}
			} catch (Exception e) {
				LOGGER.error("Connection to androidtv failed", e);
			}
		}
	}

	/**
	 * Try to connect or start pairing process
	 * @return true, if connection succeeded.
	 * @throws AndroidTvException 
	 */
	protected boolean connect() throws AndroidTvException {
		// Try to connect
		ConnectionStatus connectionStatus = this.attemptToConnect();
		if (connectionStatus == ConnectionStatus.SUCCESS) {
			LOGGER.info("Connected to " + target.toString());
			return true;
		}
		
		// else try to pair
		if(connectionStatus == ConnectionStatus.NEEDS_PAIRING) {
			this.attemptToPair();
		}
		
		return false;
	}
	

	/**
	 * Attempts to establish connection the Anymote server.
	 * 
	 * @return result of connection attempt.
	 * @throws AndroidTvException 
	 */
	public ConnectionStatus attemptToConnect() throws AndroidTvException {
		LOGGER.info("Attemp to connect");
		ConnectionStatus status = ConnectionStatus.ERROR;

		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(this.keyStore.getKeyManagers(), this.keyStore.getTrustManagers(), null);
			SSLSocketFactory factory = sslContext.getSocketFactory();
			this.sslsock = (SSLSocket) factory.createSocket(this.target.getAddress().getHostAddress(), this.target.getPort());
			this.sslsock.setUseClientMode(true);
			this.sslsock.setKeepAlive(true);
			this.sslsock.setTcpNoDelay(true);
			this.sslsock.startHandshake();

			if (this.sslsock.isConnected()) {
				status = ConnectionStatus.SUCCESS;
			}
		} catch (NoSuchAlgorithmException | KeyManagementException | ConnectException e) {
			throw new AndroidTvException(e);
		} catch (SSLException e) {
			LOGGER.error("(SSL) Could not create socket to " + target.getName(), e);
			status = ConnectionStatus.NEEDS_PAIRING;
		} catch (IOException e) {
			if (e.getMessage().startsWith("SSL handshake")) {
				LOGGER.error("(IOE) SSL handshake failed while connecting to " + target.getName(), e);
				status = ConnectionStatus.NEEDS_PAIRING;
			} else {
				throw new AndroidTvException(e);
			}
		}

		if (status != ConnectionStatus.SUCCESS) {
			if (this.sslsock != null && this.sslsock.isConnected()) {
				try {
					this.sslsock.close();
				} catch (IOException e) {
					LOGGER.error("(IOE) Could not close socket", e);
				}
			}
			this.sslsock = null;
		}

		return status;
	}
	
	/**
	 * Attempts to establish pairing with the server.
	 * 
	 * @param listener
	 *            Listener for device pairing state.
	 * @return PairingStatus device pairing result.
	 * @throws AndroidTvException 
	 */
	public void attemptToPair() throws AndroidTvException {
		LOGGER.info("Attemp to pair");
		
		SSLSocketFactory socketFactory;
		SSLSocket socket;
		PairingContext context;

		try {
			try {
				socketFactory = DummySSLSocketFactory.fromKeyManagers(keyStore.getKeyManagers());
			} catch (GeneralSecurityException e) {
				throw new IllegalStateException("Cannot build socket factory", e);
			}

			Socket s = new java.net.Socket(this.target.getAddress().getHostAddress(), this.target.getPort() + 1);
			socket = (SSLSocket) socketFactory.createSocket(s, target.getAddress().getHostAddress(), this.target.getPort() + 1, true);
			context = PairingContext.fromSslSocket(socket, false);

			PoloWireInterface protocol = WireFormat.JSON.getWireInterface(context);

			ClientPairingSession pairingSession = new ClientPairingSession(protocol, context, "AnyMote", JavaPlatform.getString(JavaPlatform.NAME));

			EncodingOption hexEnc = new EncodingOption(EncodingOption.EncodingType.ENCODING_HEXADECIMAL, 4);
			pairingSession.addInputEncoding(hexEnc);
			pairingSession.addOutputEncoding(hexEnc);

			this.pairingListener.setPairingContext(context);
			pairingSession.doPair(pairingListener);

		} catch (PoloException | IOException e) {
			throw new AndroidTvException(e);
		}
	}
	
	/**
	 * Instantiate the message sender with the socket output stream
	 * @param sslSocket
	 * @return
	 */
	private boolean instantiateMessageSender(SSLSocket sslSocket) {
    	LOGGER.debug("Instantiate the message sender");

    	try {
        	this.messageSender = new MessageSender(sslSocket.getOutputStream());
        	this.messageSender.configure();
        } catch (IOException e) {
            LOGGER.error("Unable to create sender", e);
            return false;
        }
        
        return true;
    }
	
	/**
	 * Disconnect from the Anymote server.
	 */
	public void disconnect() {
		LOGGER.info("Disconnect");

		try {
			if (this.sslsock != null) {
				this.sslsock.close();
			}
		} catch (IOException e) {
			LOGGER.error("(IOE) Failed to close socket", e);
		}
		this.sslsock = null;
		this.messageSender = null;
	}

	/**
     * Sends Url to Anymote service. This is url is a serialzed representation
     * of Intent.
     * 
     * @param url
     */
    public void sendUrl(final String url) {
        final Message msg = Message.obtain(MessageType.URL, url);
        messageSender.handleMessage(msg);
    }

    /**
     * Sends key press event to Anymote service.
     * 
     * @param key code of the key that was pressed.
     */
    public void sendKeyPress(final Code key) {
        final Message msg = Message.obtain(MessageType.KEYPRESS, key);
        messageSender.handleMessage(msg);
    }
}
