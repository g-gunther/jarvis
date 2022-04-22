package com.gguproject.jarvis.plugin.freeboxv6.listener;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.freeboxv6.service.KodiProcess;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.listener.AsbtractSpeechCommandProcessor;

import javax.inject.Named;

@Named
public class NetflixSpeechCommandProcessor extends AsbtractSpeechCommandProcessor {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(NetflixSpeechCommandProcessor.class);

	private final KodiProcess kodiProcess;

	public NetflixSpeechCommandProcessor(KodiProcess kodiProcess) {
		super("NETFLIX", "SET");
		this.kodiProcess = kodiProcess;
	}

	@Override
	public void process(DistributedEvent event, Command command) {
		this.kodiProcess.startAndSetNetflix();
	}
}
