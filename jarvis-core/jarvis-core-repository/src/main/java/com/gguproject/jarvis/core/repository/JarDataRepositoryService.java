package com.gguproject.jarvis.core.repository;

import java.io.File;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class JarDataRepositoryService extends AbstractJarRepositoryService{
	private final static Logger LOGGER = Logger.getLogger(JarDataRepositoryService.class.getName());
	
	/**
	 * Download the latest jar 
	 * @param jarName
	 * @param version
	 */
	public static Optional<File> downloadVersion(String jarName, String version) {
		FileContainer targetFileContainer = new FileContainer();

		execute((urlBuilder) -> {
			HttpRequest request = HttpRequest.newBuilder()
				      .uri(URI.create(urlBuilder.append("/jar/").append(jarName).append("/version/").append(version).append("/data/download").get()))
				      .GET()
				      .timeout(Duration.ofSeconds(2))
				      .build();
			
			File targetFile = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
			targetFile.deleteOnExit();
			targetFileContainer.setFile(targetFile);
			LOGGER.fine(String.format("Target file: %s", targetFileContainer));
			
	        HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(targetFileContainer.getFilePath()));
	        if(response.statusCode() != 200) {
	        	LOGGER.info(String.format("No data file found for: %s %s", jarName, version));
	        	targetFileContainer.setFile(null);
	        }
		});
		
		return Optional.ofNullable(targetFileContainer.getFile());
	}
}
