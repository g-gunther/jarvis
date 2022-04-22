package com.gguproject.jarvis.plugin.cec.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;

import javax.inject.Named;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.cec.event.PowerStatus;

@Named
public class CecService {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(CecService.class);
	
	/**
	 * 
	 * @return
	 * @throws CecException 
	 */
	public PowerStatus getPowerStatus() throws CecException {
		try {
			Process process = this.buildProcess("echo 'pow' | cec-client -s RPI");

			try(BufferedReader br = new BufferedReader (new InputStreamReader (process.getInputStream()))){
			      String line;
			      boolean startRequestFound = false;
			      while ((line = br.readLine ()) != null) {
			    	  if(line.endsWith("<< 10:8f")) {
			    		  startRequestFound = true;
			    	  } else if(startRequestFound) {
			    		  String returnCode = line.substring(line.lastIndexOf(" ") + 1);
			    		  return CecPowerStatus.findByCode(returnCode)
			    				  .map(o -> o.status)
			    				  .orElseThrow(() -> new CecException("Can't find power status from code: " + returnCode));
			    	  }
			      }  
		      }
		} catch(IOException e) {
			throw new CecException("An error occurs while processing cec power status request", e);
		}
		
		throw new CecException("Not able to find any power status");
	}
	
	/**
	 * 
	 * @return
	 * @throws CecException 
	 */
	public boolean isActiveSource() throws CecException {
		try {
			Process process = this.buildProcess("echo 'scan' | cec-client -s RPI");

			try(BufferedReader br = new BufferedReader (new InputStreamReader (process.getInputStream()))){
			      String line;
			      while ((line = br.readLine ()) != null) {
			    	  if("active source: yes".equals(line)) {
			    		  return true;
			    	  }
			      }  
		      }
		} catch(IOException e) {
			throw new CecException("An error occurs while processing cec scan request", e);
		}
		
		return false;
	}
	
	/**
	 * @throws CecException 
	 * 
	 */
	public void turnDeviceOn() throws CecException {
		this.exec("echo 'on 0' | cec-client -s RPI");
	}
	
	/**
	 * @throws CecException 
	 * 
	 */
	public void turnDeviceOff() throws CecException {
		this.exec("echo 'standby 0' | cec-client -s RPI");
	}
	
	/**
	 * 
	 * @throws CecException
	 */
	public void setAsActiveSource() throws CecException {
		this.exec("echo 'as' | cec-client -s RPI");
	}
	
	/**
	 * 
	 * @param command
	 * @throws CecException
	 */
	private void exec(String command) throws CecException {
		try {
			Process process = this.buildProcess(command);
			process.waitFor();
		} catch(IOException | InterruptedException e) {
			throw new CecException("An error occurs while processing request: " + command, e);
		}
	}
	
	/**
	 * 
	 * @param command
	 * @param lineProcessor
	 * @throws CecException
	 * @throws IOException 
	 */
	private Process buildProcess(String command) throws IOException {
		LOGGER.debug("Run command: {}", command);
		
		ProcessBuilder builder = new ProcessBuilder()
			.redirectErrorStream(true)
			.command(Arrays.asList("/bin/sh", "-c", command));
		return builder.start();
	}
	
	/**
	 * 
	 * @author guillaumegunther
	 *
	 */
	public enum CecPowerStatus {
		ON("01:46", PowerStatus.ON),
		OFF("01:90:01", PowerStatus.OFF);
		
		private String cecCode;
		private PowerStatus status;
		
		private CecPowerStatus(String code, PowerStatus status) {
			this.cecCode = code;
			this.status = status;
		}
		
		public static Optional<CecPowerStatus> findByCode(String code){
			for(var status : values()) {
				if(status.cecCode.equals(code)) {
					return Optional.of(status);
				}
			}
			return Optional.empty();
		}
	}
}
