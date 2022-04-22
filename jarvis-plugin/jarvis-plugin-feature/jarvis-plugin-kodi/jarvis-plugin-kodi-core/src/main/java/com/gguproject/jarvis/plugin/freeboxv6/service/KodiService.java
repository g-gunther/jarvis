package com.gguproject.jarvis.plugin.freeboxv6.service;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.freeboxv6.KodiPluginConfiguration;
import com.gguproject.jarvis.plugin.freeboxv6.KodiPluginConfiguration.PropertyKey;
import com.google.gson.Gson;

import javax.inject.Named;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

@Named
public class KodiService implements OnPostConstruct {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(KodiService.class);
	
	private static Gson gson = new Gson();

	private final KodiPluginConfiguration configuration;
	
	private String kodiHost;
	
	private HttpClient httpClient;

	public KodiService(KodiPluginConfiguration configuration){
		this.configuration = configuration;
	}

	@Override
	public void postConstruct() {
		this.httpClient = HttpClient.newBuilder()
				.authenticator(new Authenticator() {
	                @Override
	                protected PasswordAuthentication getPasswordAuthentication() {
	                	String password = new String(Base64.getDecoder().decode(configuration.getProperty(PropertyKey.password)));
	                    return new PasswordAuthentication(
	                            configuration.getProperty(PropertyKey.user),
	                            password.toCharArray());
	                }
	            })
				.build();
		
		this.kodiHost = this.configuration.getProperty(PropertyKey.host);
	}
	
	/**
	 * 
	 * @throws KodiException
	 */
	public boolean isStarted() {
		KodiRequest kodiRequest = new KodiRequest("JSONRPC.Ping");
		try {
			this.post(kodiRequest);
		} catch (KodiException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @throws KodiException
	 */
	public void quit() throws KodiException {
		KodiRequest kodiRequest = new KodiRequest("Application.Quit");
		this.post(kodiRequest);
	}
	
	/**
	 * 
	 * @throws KodiException
	 */
	public void playPause() throws KodiException {
		KodiRequest kodiRequest = new KodiRequest("Player.PlayPause")
				.param("playerid", "1");
		this.post(kodiRequest);
	}
	
	/**
	 * 
	 * @throws KodiException
	 */
	public void displayHome() throws KodiException {
		KodiRequest kodiRequest = new KodiRequest("GUI.ActivateWindow")
				.param("window", "home")
				.param("parameters", Arrays.asList("home"));
		this.post(kodiRequest);
	}
	
	/**
	 * 
	 * @throws KodiException
	 */
	public void displayNetflix() throws KodiException {
		KodiRequest kodiRequest = new KodiRequest("GUI.ActivateWindow")
				.param("window", "videos")
				.param("parameters", Arrays.asList("plugin://plugin.video.netflix/"));
		this.post(kodiRequest);
	}
	
	/**
	 * 
	 * @throws KodiException
	 */
	public void displayNetflixForProfile(String profilId) throws KodiException {
		KodiRequest kodiRequest = new KodiRequest("GUI.ActivateWindow")
				.param("window", "videos")
				.param("parameters", Arrays.asList("plugin://plugin.video.netflix/directory/home/?profile_id=" + profilId));
		this.post(kodiRequest);
	}
	
	/**
	 * 
	 * @throws KodiException
	 */
	public void actionSelect() throws KodiException {
		KodiRequest kodiRequest = new KodiRequest("Input.ExecuteAction")
				.param("action", "select");
		this.post(kodiRequest);
	}
	
	/**
	 * 
	 * @throws KodiException
	 */
	public void actionDown() throws KodiException {
		KodiRequest kodiRequest = new KodiRequest("Input.ExecuteAction")
				.param("action", "down");
		this.post(kodiRequest);
	}
	
	/**
	 * 
	 * @throws KodiException
	 */
	public void activateTv() throws KodiException {
		KodiRequest kodiRequest = new KodiRequest("Addons.ExecuteAddon")
				.param("addonid", "script.json-cec")
				.param("params", Map.of("command", "activate"));
		this.post(kodiRequest);
	}
	
	/**
	 * 
	 * @throws KodiException
	 */
	public void listNetflixUsers() throws KodiException {
		KodiRequest kodiRequest = new KodiRequest("Files.GetDirectory")
			.param("directory", "plugin://plugin.video.netflix/")
			.param("media", "files");
		this.post(kodiRequest);
	}
	
	/**
	 * Submit a post request to kodi
	 * @param body
	 * @throws KodiException
	 */
	private String post(KodiRequest kodiRequest) throws KodiException {
		LOGGER.debug("Post to kodi: {}", kodiRequest);
		
		try {
			HttpRequest request = HttpRequest.newBuilder()
				      .uri(URI.create(this.kodiHost))
				      .header("Content-Type", "application/json")
				      .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(kodiRequest)))
				      .build();
			HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
			
			if(response.statusCode() < 200 && response.statusCode() >= 300) {
				throw new KodiException("Kodi returns a " + response.statusCode() + " status with body: " + response.body());
			}
			
			return response.body();
		} catch (IOException | InterruptedException e) {
			throw new KodiException("An error occurs while sending the request: " + kodiRequest, e);
		}
	}
}
