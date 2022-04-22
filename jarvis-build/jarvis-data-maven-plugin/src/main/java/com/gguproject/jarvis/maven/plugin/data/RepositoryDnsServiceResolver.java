package com.gguproject.jarvis.maven.plugin.data;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import net.posick.mdns.Lookup;
import net.posick.mdns.ServiceInstance;

public class RepositoryDnsServiceResolver {

	private static RepositoryDnsServiceResolver instance;
	
	public static RepositoryDnsServiceResolver get(Log log) {
		if(instance == null) {
			instance = new RepositoryDnsServiceResolver(log);
		}
		return instance;
	}
	
	private Log log;
	
	private String repositoryAddress;
	private int repositoryPort;
	
	public RepositoryDnsServiceResolver(Log log) {
		this.log = log;
	}
	
	/**
	 * Resolve the repository
	 * @param serviceName
	 * @throws MojoExecutionException 
	 */
	public void resolve(String serviceName) throws MojoExecutionException {
		this.log.info(String.format("Resolve repository service with name: %s", serviceName));
		
		try (Lookup lookup = new Lookup(serviceName)){
			ServiceInstance[] services = lookup.lookupServices();
			
			if(services.length == 0) {
				throw new MojoExecutionException(String.format("No repository server found for %s", serviceName));
			} else if(services.length > 1) {
				throw new MojoExecutionException(String.format("Several repository server found for %s - do not register them", serviceName));
			} else {
				ServiceInstance service = services[0];
				if(service.getAddresses().length == 1) {
					repositoryPort = service.getPort();
				    repositoryAddress = service.getAddresses()[0].getHostAddress();
				    this.log.info(String.format("Repository service found : %s %s", repositoryAddress, repositoryPort));
				} else {
					throw new MojoExecutionException(String.format("Several or no addresses found for repository service %s - leave", serviceName));
				}
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Error while trying to find repository service", e);
		}
	}
	
	public String getAddress() {
		return String.format("%s:%s",this.repositoryAddress, this.repositoryPort);
	}
}
