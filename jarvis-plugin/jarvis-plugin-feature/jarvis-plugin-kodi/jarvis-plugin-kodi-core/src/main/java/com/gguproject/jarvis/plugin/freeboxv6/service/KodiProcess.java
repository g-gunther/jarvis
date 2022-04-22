package com.gguproject.jarvis.plugin.freeboxv6.service;

import com.gguproject.jarvis.helper.shell.AbstractShellService;
import com.gguproject.jarvis.helper.shell.ShellCommandException;
import com.gguproject.jarvis.plugin.freeboxv6.KodiPluginConfiguration;
import com.gguproject.jarvis.plugin.freeboxv6.KodiPluginConfiguration.PropertyKey;

import javax.inject.Named;
import java.io.IOException;

@Named
public class KodiProcess extends AbstractShellService {

	private static final String SLEEP_DURATION = "1";

	private final KodiService kodiService;

	private final KodiPluginConfiguration configuration;

	public KodiProcess(KodiService kodiService, KodiPluginConfiguration configuration){
		this.kodiService = kodiService;
		this.configuration = configuration;
	}

	/**
	 * Start the tv and select netflix
	 * @throws KodiException
	 */
	public void startAndSetNetflix() throws KodiException {
		this.kodiService.activateTv();
		
		try {
			this.sleep();
		} catch (ShellCommandException e) {
			throw new KodiException("An error occurs while waiting", e);
		}
		
		this.kodiService.displayNetflixForProfile(this.configuration.getProperty(PropertyKey.netflixProfileId));
	}
	
	/**
	 * 
	 * @throws KodiException
	 */
	private void startKodiProcess() throws KodiException {
		try {
			new ProcessBuilder("kodi --standalone").start();
		} catch (IOException e) {
			throw new KodiException("An exception occurs while starting the kodi process", e);
		}
	}
	
	@Override
	protected String getSleepDuration() {
		return SLEEP_DURATION;
	}
}
