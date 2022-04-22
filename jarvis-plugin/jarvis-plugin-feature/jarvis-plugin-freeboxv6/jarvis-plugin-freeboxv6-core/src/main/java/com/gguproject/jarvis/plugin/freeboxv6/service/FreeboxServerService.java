package com.gguproject.jarvis.plugin.freeboxv6.service;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.freeboxv6.FreeboxPluginConfiguration;
import com.gguproject.jarvis.plugin.freeboxv6.FreeboxPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.freeboxv6.service.dto.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import javax.inject.Named;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.function.Function;

@Named
public class FreeboxServerService extends HttpRequestService implements OnPostConstruct {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(FreeboxServerService.class);
	
	private static Gson gson = new Gson();

	private static String freeboxAuthHeaderName = "X-Fbx-App-Auth";

	private static String sessionToken = "HxEG3RqBACiLf2BzZ3JxzpPmksvSHa3CM3mlsA9iEMRECVAkYMNDscPmcQkhNjYE";

	private final FreeboxPluginConfiguration configuration;
	
	private String host;
	private String appToken;
	private String appId;
	
	private HttpClient httpClient;

	public FreeboxServerService(FreeboxPluginConfiguration configuration){
		this.configuration = configuration;
	}

	@Override
	public void postConstruct() {
		this.httpClient = HttpClient.newBuilder().build();
		this.host = this.configuration.getProperty(PropertyKey.Server.host);
		this.appToken = this.configuration.getProperty(PropertyKey.Server.appToken);
		this.appId = this.configuration.getProperty(PropertyKey.Server.appId);
	}

	@Override
	protected String getHost(){
		return this.host;
	}

	public FreeboxServerListPlayerResponse listPlayers() {
		Request request = new Request()
				.path("/api/v8/player")
				.header(freeboxAuthHeaderName, this.getSessionToken());
		FreeboxServerResponse<FreeboxServerListPlayerResponse> response = this.get(request, FreeboxServerListPlayerResponse.class);
		return response.getResult();
	}

	public PlayerStatus playerStatus(String playerId) {
		Request request = new Request()
				.path("/api/v8/player/"+ playerId +"/api/v6/status/")
				.header(freeboxAuthHeaderName, this.getSessionToken());
		FreeboxServerResponse<FreeboxServerPlayerStatusReponse> response = this.get(request, FreeboxServerPlayerStatusReponse.class);
		return response.getResult().getPowerState();
	}

	public void controlPlayer(String playerId, PlayerCommand command) {
		Request request = new Request()
				.path("/api/v8/player/"+ playerId +"/api/v6/control/mediactrl/")
				.header(freeboxAuthHeaderName, this.getSessionToken())
				.header("Content-Type", "application/json");
		this.post(request, new FreeboxServerPlayerCommandRequest(command), FreeboxServerEmptyResponse.class);
	}

	public void openUrl(String playerId, String url, String type) {
		Request request = new Request()
				.path("/api/v8/player/"+ playerId +"/api/v6/control/open")
				.header(freeboxAuthHeaderName, this.getSessionToken())
				.header("Content-Type", "application/json");
		this.post(request, new FreeboxServerPlayerUrlRequest(url, type), FreeboxServerEmptyResponse.class);
	}

	/**
	 *
	 * @param request
	 * @param responseType
	 * @param <T>
	 * @return
	 * @throws FreeboxServerException
	 */
	private <T> FreeboxServerResponse<T> get(Request request, Class<T> responseType) {
		return this.executeRequest(request, (r) -> super.get(r), responseType);
	}

	/**
	 *
	 * @param request
	 * @param data
	 * @param responseType
	 * @param <T>
	 * @return
	 * @throws FreeboxServerException
	 */
	private <T> FreeboxServerResponse<T> post(Request request, Object data, Class<T> responseType) {
		return this.executeRequest(request, (r) -> super.post(r, data), responseType);
	}

	/**
	 *
	 * @param request
	 * @param requestExecutor
	 * @param responseClazz
	 * @param <T>
	 * @return
	 * @throws FreeboxServerException
	 */
	private <T> FreeboxServerResponse<T> executeRequest(Request request, Function<Request, HttpResponse<String>> requestExecutor, Class<T> responseClazz) {
		HttpResponse<String> response = requestExecutor.apply(request);

		// might be an authentication issue - session token is not valid anymore?
		if(response.statusCode() == 403){
			FreeboxServerErrorResponse errorResponse = gson.fromJson(response.body(), FreeboxServerErrorResponse.class);
			// seems an auth is request & the freebox auth header was filled
			if(!errorResponse.isSuccess() && AuthenticationStatus.AUTH_REQUIRED.equals(errorResponse.getErrorCode()) && request.hasHeader(freeboxAuthHeaderName)){
				// negociate the token & execute the request again
				this.openSession();
				request.header(freeboxAuthHeaderName, this.getSessionToken());
				response = requestExecutor.apply(request);
			}
		}

		if(response.statusCode() < 200 || response.statusCode() >= 300){
			LOGGER.error("Invalid status code: {} with body: {}", response.statusCode(), response.body());
			throw new FreeboxServerException("Invalid response status code: " + response.statusCode());
		}

		Type responseType = TypeToken.getParameterized(FreeboxServerResponse.class, responseClazz).getType();
		return gson.fromJson(response.body(), responseType);
	}

	/**
	 *
	 * @return
	 * @throws FreeboxServerException
	 */
	private String getSessionToken() {
		if(sessionToken == null){
			this.openSession();
		}
		return sessionToken;
	}

	/**
	 *
	 * @return
	 * @throws FreeboxServerException
	 */
	private void openSession() {
		String challenge = this.getChallenge();

		String password = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, this.appToken).hmacHex(challenge);
		FreeboxServerSessionRequest requestData = new FreeboxServerSessionRequest(this.appId, password);

		Request request = new Request()
				.path("/api/v8/login/session/")
				.header("Content-Type", "application/json");
		FreeboxServerResponse<FreeboxServerSessionResponse> response = this.post(request, requestData, FreeboxServerSessionResponse.class);

		LOGGER.debug("Session permissions : {}", response.getResult().getPermissions());
		sessionToken = response.getResult().getSessionToken();
	}

	/**
	 *
	 * @return
	 * @throws FreeboxServerException
	 */
	private String getChallenge() {
		Request request = new Request().path("/api/v8/login/");
		FreeboxServerResponse<FreeboxServerChallengeResponse> response = this.get(request, FreeboxServerChallengeResponse.class);
		return response.getResult().getChallenge();
	}
}
