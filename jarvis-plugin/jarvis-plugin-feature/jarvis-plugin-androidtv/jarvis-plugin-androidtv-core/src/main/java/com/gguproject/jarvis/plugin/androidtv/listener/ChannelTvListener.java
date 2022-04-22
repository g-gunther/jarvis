package com.gguproject.jarvis.plugin.androidtv.listener;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.androidtv.service.AndroidTvRemoteControlService;
import com.gguproject.jarvis.plugin.androidtv.service.TvChannelConverter;
import com.gguproject.jarvis.plugin.androidtv.util.AndroidTvException;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.listener.AsbtractSpeechCommandProcessor;

import javax.inject.Named;

@Named
public class ChannelTvListener extends AsbtractSpeechCommandProcessor {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(ChannelTvListener.class);

	private final TvChannelConverter converter;

	private final AndroidTvRemoteControlService androidTvRemoteControlService;
	
	public ChannelTvListener(TvChannelConverter converter, AndroidTvRemoteControlService androidTvRemoteControlService) {
		super("TV", "SET");
		this.converter = converter;
		this.androidTvRemoteControlService = androidTvRemoteControlService;
	}
	
	@Override
	public void process(DistributedEvent event, Command data) {
		LOGGER.info("Received channel event: {}", data.getData());
		
		Integer channel = this.converter.findChannelByName(data.getData());
		if(channel == null) {
			LOGGER.error("Not able to find channel value of name: {}", data.getData());
			return;
		}
		
		try {
			this.androidTvRemoteControlService.sendChannel(channel);
		} catch (AndroidTvException e) {
			LOGGER.error("An error occurs while changing the tv channel", e);
		}
	}
}