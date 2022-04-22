package com.gguproject.jarvis.plugin.freeboxv6.service;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.freeboxv6.FreeboxPluginConfiguration;
import com.gguproject.jarvis.plugin.freeboxv6.FreeboxPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.freeboxv6.service.dto.RemoteCommand;
import com.google.gson.Gson;

import javax.inject.Named;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

@Named
public class FreeboxRemoteService extends HttpRequestService implements OnPostConstruct {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(FreeboxRemoteService.class);
	
	private static Gson gson = new Gson();

	private final FreeboxPluginConfiguration configuration;
	
	private String host;
	private String code;
	
	private HttpClient httpClient;

	public FreeboxRemoteService(FreeboxPluginConfiguration configuration){
		this.configuration = configuration;
	}

	@Override
	public void postConstruct() {
		this.httpClient = HttpClient.newBuilder().build();
		this.host = this.configuration.getProperty(PropertyKey.Remote.host);
		this.code = this.configuration.getProperty(PropertyKey.Remote.code);
	}

	@Override
	protected String getHost(){
		return this.host;
	}

	public void power() {
		this.execute(RemoteCommand.POWER);
	}

	public void home() {
		this.execute(RemoteCommand.HOME);
	}

	public void playPause(){
		this.execute(RemoteCommand.PLAY_PAUSE);
	}

	public void back() {
		this.execute(RemoteCommand.BACK);
	}

	public void test(){
		this.execute(RemoteCommand.TOUCH_1);
	}

	public void netflix() {
		this.home();
		this.back();
		this.execute(RemoteCommand.RIGHT);
		this.execute(RemoteCommand.DOWN, 3);
		this.ok();
	}

	public void ok() throws FreeboxRemoteException {
		this.execute(RemoteCommand.OK);
	}

	/**
	 *
	 * @param command
	 * @throws FreeboxRemoteException
	 */
	private void execute(RemoteCommand command) {
		this.execute(command, 1);
	}

	/**
	 *
	 * @param command
	 * @param repeat
	 * @throws FreeboxRemoteException
	 */
	private void execute(RemoteCommand command, int repeat) {
		Request request = new Request()
				.path("/pub/remote_control")
				.queryParam("code", this.code)
				.queryParam("key", command.getCode())
				.queryParam("repeat", "" + repeat);

		HttpResponse<String> response = super.get(request);

		if(response.statusCode() < 200 || response.statusCode() >= 300){
			throw new FreeboxRemoteException("Invalid status code: " + response.statusCode() + " for request: " + request);
		}
	}
}
