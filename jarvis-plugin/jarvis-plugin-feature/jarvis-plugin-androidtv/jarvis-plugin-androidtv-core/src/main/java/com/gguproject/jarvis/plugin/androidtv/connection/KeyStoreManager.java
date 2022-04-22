/*
 * Copyright (C) 2012 Google Inc.  All rights reserved.
 * Copyright (C) 2012 ENTERTAILION, LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gguproject.jarvis.plugin.androidtv.connection;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;
import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.androidtv.AndroidTvPluginConfiguration;
import com.gguproject.jarvis.plugin.androidtv.AndroidTvPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.androidtv.util.JavaPlatform;
import com.gguproject.jarvis.plugin.core.PluginApplicationContextAware;
import com.google.polo.ssl.SslUtil;

import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * Key store manager. It manages client and server certificates.
 */
@Named
public final class KeyStoreManager implements OnPostConstruct {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(KeyStoreManager.class);

	private final AndroidTvPluginConfiguration configuration;
	
	/** Keystore file */
    private String keystoreFilename;
    
    /** Keystore password */
    private String keystorePassword;

    /**
     * Alias for the remote controller (local) identity in the {@link KeyStore}.
     */
    private static final String LOCAL_IDENTITY_ALIAS = "anymote-remote";

    /**
     * Alias pattern for anymote server identities in the {@link KeyStore}
     */
    private static final String REMOTE_IDENTITY_ALIAS_PATTERN = "anymote-server-%X";

    private KeyManager[] keyManagers;
    private TrustManager[] trustManagers;
    private KeyStore keyStore;

    @Inject
    public KeyStoreManager(AndroidTvPluginConfiguration configuration){
        this.configuration = configuration;
    }

    public void postConstruct() {
    	LOGGER.debug("Initialize the keyStoreManager");
    	
    	this.keystoreFilename = this.configuration.getProperty(AndroidTvPluginConfiguration.PropertyKey.keystore);
    	this.keystorePassword = this.configuration.getProperty(AndroidTvPluginConfiguration.PropertyKey.keystorePassword);
    	
        this.load();
        if (!this.hasLocalIdentityAlias()) {
        	LOGGER.debug("No local identity found, generate application certificate");
            this.generateAppCertificate();
        }
        
    	try {
			this.collectKeyManagers();
			this.collectTrustManagers();
		} catch (GeneralSecurityException e) {
			throw TechnicalException.get().message("Security exception during initialization").exception(e).build();
		}
    }
    
    /**
     * Loads key store from storage, or creates new one if storage is missing
     * key store or corrupted.
     */
    private void load() {
    	LOGGER.info("Load keystore");
    	
        try {
            this.keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Unable to get default instance of KeyStore", e);
        }
        
        try {
        	AbstractPluginConfiguration configuration = PluginApplicationContextAware.getApplicationContext().getBean(AbstractPluginConfiguration.class);
        	File keystore = configuration.getConfigurationFile(configuration.getProperty(PropertyKey.keystore))
                    .orElseThrow(() -> new IllegalArgumentException(String.format("No keystore found for androidtv plugin: %s", configuration.getProperty(PropertyKey.keystore))));
        	
            FileInputStream fis = JavaPlatform.openFileInput(keystore.getAbsolutePath());
            this.keyStore.load(fis, this.keystorePassword.toCharArray());
        } catch (IOException e) {
            LOGGER.info("Unable open keystore file", e);
            this.keyStore = null;
        } catch (GeneralSecurityException e) {
        	LOGGER.info("Unable open keystore file", e);
        	this.keyStore = null;
        }

        // No keys found: generate.
        if (this.keyStore == null) {
        	LOGGER.debug("No key store found, generate");
            try {
            	this.keyStore = this.createKeyStore();
                this.store();
            } catch (GeneralSecurityException e) {
                throw new IllegalStateException("Unable to create identity KeyStore", e);
            }
        }
    }

    /**
     * Verify if local certificate is available.
     * @return true, if certificate is available.
     */
    private boolean hasLocalIdentityAlias() {
        try {
            if (!this.keyStore.containsAlias(LOCAL_IDENTITY_ALIAS)) {
                LOGGER.debug("Key store missing identity for " + LOCAL_IDENTITY_ALIAS);
                return false;
            }
        } catch (KeyStoreException e) {
        	LOGGER.debug("Key store exception occurred", e);
            return false;
        }
        return true;
    }

    /**
     * Create application-specific certificate that will be used to authenticate
     * user.
     */
    private void generateAppCertificate() {
    	LOGGER.debug("Generating application certificate");
    	
        this.clearKeyStore();
        
        try {
            KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = kg.generateKeyPair();

            String name = getCertificateName(getUniqueId());
            X509Certificate cert = SslUtil.generateX509V3Certificate(keyPair, name);
            Certificate[] chain = { cert };

            this.keyStore.setKeyEntry(LOCAL_IDENTITY_ALIAS, keyPair.getPrivate(), this.keystorePassword.toCharArray(), chain);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to create identity KeyStore", e);
        }
        
        this.store();
    }

    private KeyStore createKeyStore() throws GeneralSecurityException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyStore.load(null, this.keystorePassword.toCharArray());
        } catch (IOException e) {
            throw new GeneralSecurityException("Unable to create empty keyStore", e);
        }
        return keyStore;
    }

    /**
     * Store the keystore in file
     */
    private void store() {
        try {
            FileOutputStream fos = JavaPlatform.openFileOutput(this.keystoreFilename, JavaPlatform.MODE_PRIVATE);
            keyStore.store(fos, this.keystorePassword.toCharArray());
            fos.close();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to store keyStore", e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to store keyStore", e);
        }
    }

    /**
     * Returns the name that should be used in a new certificate.
     * <p>
     * The format is: "CN=anymote/PRODUCT/DEVICE/MODEL/unique identifier"
     */
    private final String getCertificateName(String id) {
        return "CN=anymote/" + JavaPlatform.getString(JavaPlatform.CERTIFICATE_NAME) + "/" + id;
    }

    /**
     * @throws GeneralSecurityException
     */
    private void collectKeyManagers() throws GeneralSecurityException {
        if (this.keyStore == null) {
            throw new NullPointerException("null mKeyStore");
        }
        KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory
                .getDefaultAlgorithm());
        factory.init(this.keyStore, this.keystorePassword.toCharArray());
        this.keyManagers = factory.getKeyManagers();
    }

    /**
     * @throws GeneralSecurityException
     */
    private void collectTrustManagers() throws GeneralSecurityException {
        // Build a new set of TrustManagers based on the KeyStore.
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        this.trustManagers = tmf.getTrustManagers();
    }

    /**
     * Stores the remote device certificate in keystore.
     * @param peerCert
     */
    public void storeCertificate(final Certificate peerCert) {
        try {
            String alias = String.format(KeyStoreManager.REMOTE_IDENTITY_ALIAS_PATTERN, peerCert.hashCode());
            if (keyStore.containsAlias(alias)) {
            	LOGGER.debug("Deleting existing entry for " + alias);
                this.keyStore.deleteEntry(alias);
            }
            
            LOGGER.debug("Adding cert to keystore: " + alias);
            this.keyStore.setCertificateEntry(alias, peerCert);
            this.store();

            try {
            	this.collectKeyManagers();
                this.collectTrustManagers();
            } catch (GeneralSecurityException e) {
                // ignore.
            }
        } catch (KeyStoreException e) {
        		LOGGER.debug("Storing cert failed", e);
        }
    }

    /**
     * Clear key store
     */
    private void clearKeyStore() {
        try {
            for (Enumeration<String> e = this.keyStore.aliases(); e.hasMoreElements();) {
                final String alias = e.nextElement();
                LOGGER.debug("Deleting alias: " + alias);
                this.keyStore.deleteEntry(alias);
            }
        } catch (KeyStoreException e) {
        		LOGGER.debug("Clearing certificates failed", e);
        }
        
        this.store();
    }

    private String getUniqueId() {
    		return JavaPlatform.getString(JavaPlatform.UNIQUE_ID);
    }
    
    /**
     * @return key managers loaded for this service.
     */
    public KeyManager[] getKeyManagers() {
        return keyManagers;
    }
    
    /**
     * @return trust managers loaded for this service.
     */
    public TrustManager[] getTrustManagers() {
        return trustManagers;
    }
}
