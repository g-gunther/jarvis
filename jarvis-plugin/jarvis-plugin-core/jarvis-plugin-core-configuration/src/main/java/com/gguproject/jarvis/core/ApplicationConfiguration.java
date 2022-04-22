package com.gguproject.jarvis.core;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.inject.Named;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

/**
 * Global application configuration
 * @author GGUNTHER
 */
@Named
public class ApplicationConfiguration extends AbstractConfiguration {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(ApplicationConfiguration.class);
	
	private String ipAddress;
	
	public ApplicationConfiguration(){
		try {
	  		InetAddress ip = InetAddress.getLocalHost();
			this.ipAddress = ip.getHostAddress();
			LOGGER.info("Local ip addess: {}", this.ipAddress);
  		} catch (UnknownHostException e) {
	  		LOGGER.error("No able to find the current ip address", e);
  		}
		
		this.loadPropertyFiles();
	}
	
	private void loadPropertyFiles() {
		InputStream defaultApplicationProperties = this.getConfigurationResource("jarvis/configuration.properties");
		if(defaultApplicationProperties != null) {
			LOGGER.debug("Load default configuration property file");
			this.loadProperties(defaultApplicationProperties);
		}
	}
	
	public String getIpAddress() {
		return this.ipAddress;
	}
	
	public class PropertyKey{
		public static final String tcpListenPort = "tcp.server.port";
	}
}
