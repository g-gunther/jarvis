package com.gguproject.jarvis.core.repository;

import java.io.File;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.UUID;
import java.util.logging.Logger;

import com.gguproject.jarvis.core.repository.dto.JarDto;
import com.google.gson.Gson;

public class JarVersionRepositoryService extends AbstractJarRepositoryService{
	private final static Logger LOGGER = Logger.getLogger(JarVersionRepositoryService.class.getName());
	
	private static Gson gson = new Gson();
	
	/**
	 * Download the latest jar in a temporary directory
	 * @param jarName
	 */
	public static File downloadLatestVersion(String jarName) {
		FileContainer targetFileContainer = new FileContainer();
		
		execute((urlBuilder) -> {
			HttpRequest request = HttpRequest.newBuilder()
				      .uri(URI.create(urlBuilder.append("/jar/").append(jarName).append("/version/latest/download").get()))
				      .GET()
				      .timeout(Duration.ofSeconds(2))
				      .build();
			
			File targetFile = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
			targetFile.deleteOnExit();
			targetFileContainer.setFile(targetFile);
			LOGGER.fine(String.format("Target file: %s", targetFileContainer));
			
	        httpClient.send(request, HttpResponse.BodyHandlers.ofFile(targetFileContainer.getFilePath()));
		});
		
		return targetFileContainer.getFile();
	}
	
	/**
	 * Get the latest jar data
	 * @param jarName
	 * @return
	 */
	public static JarDto getLatestVersion(String jarName) {
		JarDto jarDto = new JarDto();
		
		execute((urlBuilder) -> {
			HttpRequest request = HttpRequest.newBuilder()
				      .uri(URI.create(urlBuilder.append("/jar/").append(jarName).append("/version/latest").get()))
				      .header("Content-Type", "application/json")
				      .GET()
				      .timeout(Duration.ofSeconds(2))
				      .build();
			HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
			jarDto.copyValues(gson.fromJson(response.body(), JarDto.class));
		});
		
		return jarDto;
	}
}
