package com.gguproject.jarvis.core.repository;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JarRepositoryService extends AbstractJarRepositoryService{
	private static Gson gson = new Gson();
	
	/**
	 * Download the latest jar 
	 * @param jarName
	 * @param version
	 */
	public static Map<String, String> listAllJars() {
		Map<String, String> jars = new HashMap<>();
		
		execute((urlBuilder) -> {
			HttpRequest request = HttpRequest.newBuilder()
				      .uri(URI.create(urlBuilder.append("/jar/list").get()))
				      .header("Content-Type", "application/json")
				      .GET()
				      .timeout(Duration.ofSeconds(2))
				      .build();
			HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
			
			jars.putAll(gson.fromJson(response.body(), new TypeToken<Map<String, String>>(){}.getType()));
		});
		
		return jars;
	}
}
