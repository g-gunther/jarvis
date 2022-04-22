package com.gguproject.jarvis.plugin.spotify.service;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.spotify.SpotifyPluginConfiguration;
import com.gguproject.jarvis.plugin.spotify.SpotifyPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.spotify.service.dto.CurrentDeviceContext;
import com.gguproject.jarvis.plugin.spotify.service.dto.DeviceDto;
import com.gguproject.jarvis.plugin.spotify.service.dto.PlaylistDto;
import com.google.gson.JsonArray;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.UnauthorizedException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import org.apache.hc.core5.http.ParseException;

import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named
public class SpotifyService implements OnPostConstruct {
	/** Log */
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpotifyService.class);

	public final SpotifyPluginConfiguration configuration;
	
	/**
	 * Spotify api service
	 */
	private SpotifyApi spotifyApi;
	
	/**
	 * Api calls to generate a new access token
	 */
	private SpotifyApi spotifyAuthorizationApi;
	private AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest;

	public SpotifyService(SpotifyPluginConfiguration configuration){
		this.configuration = configuration;
	}

	@Override
	public void postConstruct() {
		this.spotifyAuthorizationApi = new SpotifyApi.Builder()
				.setClientId(this.configuration.getProperty(PropertyKey.spotifyClientId))
				.setClientSecret(this.configuration.getProperty(PropertyKey.spotifyClientSecret))
				.setRefreshToken(this.configuration.getProperty(PropertyKey.spotifyApiRefreshToken))
				.setAccessToken(null)
				.build();
		
		this.authorizationCodeRefreshRequest = this.spotifyAuthorizationApi.authorizationCodeRefresh().build();	
	}
	
	/**
	 * Get all playlist for the current user
	 * @return
	 */
	public List<PlaylistDto> getCurrentUserPlaylists() {
		List<PlaylistSimplified> playlists = this.paginate((offset, limit) -> {
			return this.spotifyApi.getListOfCurrentUsersPlaylists()
				.offset(offset)
				.limit(limit)
				// Specify the language else spotify will return the playlist name in english
				.setHeader("accept-language", this.configuration.getLanguage())
				.build()
				.execute();
		});
		
		return playlists.stream().map(item -> new PlaylistDto(item.getName(), item.getId())).collect(Collectors.toList());
	}

	public CurrentDeviceContext getCurrentDevice(){
		CurrentDeviceContext device = new CurrentDeviceContext();

		this.execute(() -> {
			CurrentlyPlayingContext context = this.spotifyApi.getInformationAboutUsersCurrentPlayback().build().execute();
			device.init(context);
		});

		return device;
	}
	
	/**
	 * List registered devices
	 * @return
	 */
	public List<DeviceDto> listDevices() {
		List<DeviceDto> devices = new ArrayList<>();
		
		this.execute(() -> {
			Device[] response = this.spotifyApi.getUsersAvailableDevices().build().execute();
			devices.addAll(
				Arrays.asList(response).stream()
				.map(d -> new DeviceDto(d))
				.collect(Collectors.toList())
			);
		});
		
		return devices;
	}
	
	/**
	 * Select a device by its id and play the music
	 * @param deviceId
	 */
	public void startMusicOnDevice(String deviceId){
		this.startPlaylistOnDevice(deviceId, null);
	}
	
	/**
	 * Start a playlist on the current registered device
	 * @param playlistId
	 */
	public void startPlaylistOnDevice(String deviceId, String playlistId) {
		this.execute(() -> {
			StartResumeUsersPlaybackRequest.Builder builder = this.spotifyApi.startResumeUsersPlayback()
				.device_id(deviceId);

			if(playlistId != null) {
				builder.context_uri("spotify:playlist:" + playlistId);
			}

			builder.build().execute();
		});
	}

	public void transfertToDevice(String deviceId){
		this.execute(() -> {
			JsonArray deviceIds = new JsonArray(1);
			deviceIds.add(deviceId);
			this.spotifyApi.transferUsersPlayback(deviceIds)
					.play(true)
					.build()
					.execute();
		});
	}

	/**
	 * Pause the current music
	 */
	public void pause() {
		this.execute(() -> {
			this.spotifyApi.pauseUsersPlayback().build().execute();
		}); 
	}


	
	/**
	 * Execute a paginated request
	 * @param request
	 * @return
	 */
	private <T> List<T> paginate(SpotifyPaginateRequest<T> request) {
		List<T> results = new ArrayList<>();
		
		this.execute(() -> {
			int limit = 20;
			int offset = -limit; // start to -limit because first operation in loop is to increment offset by limit value 
			int nbTotalResult = 50; // number greater than limit + offset to loop at least once
			
			Paging<T> pagingRequest;
			while(nbTotalResult > offset + limit) {
				offset += limit;
				
				pagingRequest = request.execute(offset, limit);
				nbTotalResult = pagingRequest.getTotal();
				
				results.addAll(Arrays.asList(pagingRequest.getItems()));
			}
		});
		
		return results;
	}
	
	/**
	 * Execute a simple request
	 * @param request
	 */
	private void execute(SpotifyRequest request) {
		try {
			if(this.spotifyApi == null) {
				this.authenticate();
			}
			
			try {
				request.execute();
			} catch(UnauthorizedException | ParseException e) {
				LOGGER.info("Access token has expired - refresh it and try again");
				this.authenticate();
				request.execute();
			}
		} catch (SpotifyWebApiException | IOException | ParseException e) {
			LOGGER.error("Error while executing spotify request: {}", request, e);
		}
	}
	
	/**
	 * Authenticate to the spotify api
	 * @throws SpotifyWebApiException
	 * @throws IOException
	 */
	private void authenticate() throws SpotifyWebApiException, IOException, ParseException {
		LOGGER.info("Generate a new access token for spotify");
		final AuthorizationCodeCredentials authorizationCodeCredentials = this.authorizationCodeRefreshRequest.execute();
		this.spotifyApi = new SpotifyApi.Builder().setAccessToken(authorizationCodeCredentials.getAccessToken()).build();
	}
	
	@FunctionalInterface
	private interface SpotifyRequest {
		public void execute() throws SpotifyWebApiException, IOException, ParseException;
	}
	
	@FunctionalInterface
	private interface SpotifyPaginateRequest<T> {
		public Paging<T> execute(int offset, int limit) throws SpotifyWebApiException, IOException, ParseException;
	}
}
