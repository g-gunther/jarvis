package com.gguproject.jarvis.core.repository;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.posick.mdns.Lookup;
import net.posick.mdns.ServiceInstance;

public class AbstractJarRepositoryService {
	private final static Logger LOGGER = Logger.getLogger(AbstractJarRepositoryService.class.getName());
	
	protected static final HttpClient httpClient = HttpClient.newHttpClient();
	
	private static String repositoryAddress;
	
	private static Integer repositoryPort;
	
	/**
	 * Resolve the repository service
	 */
	private static void resolveRepositoryService() {
		repositoryAddress = null;
		repositoryPort = null;
		
		try (Lookup lookup = new Lookup("jarvis.com.gguproject.jarvis:repository_v0.0.1._http.local.")){
			ServiceInstance[] services = lookup.lookupServices();
			
			if(services.length == 0) {
				LOGGER.info("No repository server found");
			} else if(services.length > 1) {
				LOGGER.warning("Several repository server found - do not register them");
			} else {
				ServiceInstance service = services[0];
				if(service.getAddresses().length == 1) {
					repositoryPort = service.getPort();
				    repositoryAddress = service.getAddresses()[0].getHostAddress();
				    LOGGER.info(String.format("Repository service found : %s %s", repositoryAddress, repositoryPort));
				} else {
					LOGGER.warning("Several or no addresses found for repository service - leave");
				}
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error while trying to find repository service", e);
		}
	}
	
	public static boolean checkRepositoryExists() {
		LOGGER.fine("Check if a repository can be found");
		resolveRepositoryService();
		if(repositoryAddress == null || repositoryPort == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Execute a simple request
	 * If it fails with an {@link IOException} it will try to resolve the repository service again and execute it again
	 * @param request
	 */
	protected static void execute(JarRepositoryRequest request) {
		if(repositoryAddress == null || repositoryPort == null) {
			if(!checkRepositoryExists()) {
				LOGGER.fine("Do not execute request because no repository found");
				return;
			}
		}
		
		RequestUrlBuilder urlBuilder = new RequestUrlBuilder(repositoryAddress, repositoryPort);
		
		try {
			request.execute(urlBuilder);
		} catch (IOException | InterruptedException e) {
			LOGGER.log(Level.WARNING, "An error occurs while execute request - resolve the repository service and try again", e);

			if(!checkRepositoryExists()) {
				LOGGER.fine("Do not execute request because no repository found");
				return;
			}
			
			try {
				request.execute(urlBuilder);
			} catch (IOException | InterruptedException e1) {
				LOGGER.log(Level.SEVERE, "The request fails again - leave", e);
			}
		}
	}
	
	/**
	 * Functional interface used to execute a simple request
	 */
	public interface JarRepositoryRequest {
		public void execute(RequestUrlBuilder urlBuilder) throws IOException, InterruptedException;
	}
	
	/**
	 * Utility class to build urls
	 * It takes the hostname & ports defined by configuration and allow to append a path to build a complete url
	 */
	static class RequestUrlBuilder {
		
		private StringBuilder sb = new StringBuilder();
		
		private RequestUrlBuilder(String hostname, int port) {
			sb.append("http://").append(hostname).append(":").append(port);
		}
		
		public RequestUrlBuilder append(String value) {
			this.sb.append(value);
			return this;
		}
		
		public String get() {
			return this.sb.toString();
		}
	}
	
	/**
	 * File container used to be able to manipulate files in functional interface and return it
	 */
	static class FileContainer {
		
		private File file;
		
		public FileContainer() {
		}
		
		public void setFile(File file) {
			this.file = file;
		}
		
		public File getFile() {
			return this.file != null && this.file.exists() ? this.file : null;
		}
		
		public Path getFilePath() {
			return this.file != null && this.file.exists() ? Path.of(this.file.getPath()) : null;
		}
	}
}
