package com.gguproject.jarvis.plugin.speaker.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speaker.event.SpeakerEventData;
import com.gguproject.jarvis.plugin.speaker.service.SpeakerService;

import javax.inject.Named;

/**
 * Service listener used to mute the tv
 * @author GGUNTHER
 */
@Named
public class SpeakerServiceListener extends AbstractEventListener<SpeakerEventData>{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpeakerServiceListener.class);

	private final SpeakerService speakerService;

	public SpeakerServiceListener(SpeakerService speakerService, EventBusService eventBusService) {
		super(SpeakerEventData.eventType, SpeakerEventData.class, eventBusService);
		this.speakerService = speakerService;
	}
	
	@Override
	public void onEvent(DistributedEvent event, SpeakerEventData data) {
		LOGGER.debug("Speak text: {}", data);
		
		if(data.getSpeech() == null) {
			this.speakerService.speakByCode(data.getSpeechCode());
		} else {
			this.speakerService.speak(data.getSpeech());
		}
	}
}
