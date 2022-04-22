package com.gguproject.jarvis.plugin.time.listener;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speaker.event.SpeakerEventData;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.listener.AsbtractSpeechCommandProcessor;
import com.gguproject.jarvis.plugin.time.service.SpeakerService;

import javax.inject.Named;
import java.time.OffsetTime;

@Named
public class TimeSpeechCommandProcessor extends AsbtractSpeechCommandProcessor {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(TimeSpeechCommandProcessor.class);

	private final EventBusService eventBusService;

	private final SpeakerService speakerService;

	public TimeSpeechCommandProcessor(EventBusService eventBusService, SpeakerService speakerService) {
		super("TIME", "SAY", "GIVE");
		this.eventBusService = eventBusService;
		this.speakerService = speakerService;
	}

	@Override
	public void process(DistributedEvent event, Command command) {
		OffsetTime now = OffsetTime.now();
		LOGGER.debug("Give time: {}", now);
		String text = this.speakerService.toSpeech(now);
		LOGGER.debug("Speak data {}", text);
		this.eventBusService.externalEmit(SpeakerEventData.withSpeech(text));
	}
}
