package com.gguproject.jarvis.maven.plugin.data;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

public class RepositoryResource {

	private static RepositoryResource instance;
	
	public static RepositoryResource get(Log log) {
		if(instance == null) {
			instance = new RepositoryResource(log);
		}
		return instance;
	}
	
	private Log log;
	
	public RepositoryResource(Log log) {
		this.log = log;
	}
	
	public void submitFile(SubmitFileRequest request) throws MojoExecutionException {
		this.log.info(String.format("post file with request %s", request.toString()));
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(new StringBuilder("http://")
					.append(request.getAddress())
					.append(request.getPath())
					.toString()).openConnection();
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(30000);
			connection.setRequestMethod("POST"); 
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Disposition", "form-data; filename=\"" + request.getTargetFilename() + "\"");
			
		    OutputStream output = connection.getOutputStream();
		    Files.copy(request.getFile().toPath(), output);
		    output.flush();
	
		    // Request is lazily fired whenever you need to obtain information about response.
			int responseCode = ((HttpURLConnection) connection).getResponseCode();
			if(responseCode != 200) {
				throw new MojoExecutionException(String.format("Wrong response status: %s", responseCode));
			}
		} catch (IOException e) {
			throw new MojoExecutionException(String.format("Not able to send file: %s", request.getFile()), e);
		}
	}
}
