package com.gguproject.jarvis.maven.plugin.data;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
 
/**
 * Says "Hi" to the user.
 *
 */
@Mojo( name = "repository-upload")
public class DataMojo extends AbstractMojo {
	
	@Parameter
	private String repositoryDnsServiceName;

	@Parameter
	private File file;
	
	@Parameter
	private String path;
	
	@Parameter
	private String host;
	
	@Parameter
	private String targetFilename;
	
    public void execute() throws MojoExecutionException {
        getLog().info("Start jarvis-data plugin");
        
        if(this.file == null || !this.file.exists()) {
        	getLog().info(String.format("File %s does not exist - leave", this.file.getName()));
        	return;
        }
        
        this.assertNotNull("repositoryDnsServiceName", this.repositoryDnsServiceName);
        this.assertNotNull("path", this.path);
        
        String repositoryAddress;
        if(this.host == null) {
	        RepositoryDnsServiceResolver repositoryDnsServiceResolver = RepositoryDnsServiceResolver.get(getLog());
	        repositoryDnsServiceResolver.resolve(this.repositoryDnsServiceName);
	        repositoryAddress = repositoryDnsServiceResolver.getAddress();
        } else {
        	repositoryAddress = this.host;
        }
        
        RepositoryResource repositoryResource = RepositoryResource.get(getLog());
        
        SubmitFileRequest request = SubmitFileRequest.get()
        	.address(repositoryAddress)
        	.file(this.file)
        	.path(this.path)
        	.targetFilename(this.targetFilename);
        
        repositoryResource.submitFile(request);
    }
    
    
    public void assertNotNull(String label, String value) throws MojoExecutionException {
    	if(value == null) {
    		throw new MojoExecutionException("Parameter '"+ label +"' is null");
    	}
    }
    
    public void assertExists(String label, File file) throws MojoExecutionException {
    	if(file == null || !file.exists()) {
    		throw new MojoExecutionException("File parameter '"+ label +"' is null or does not exist");
    	}
    }
}