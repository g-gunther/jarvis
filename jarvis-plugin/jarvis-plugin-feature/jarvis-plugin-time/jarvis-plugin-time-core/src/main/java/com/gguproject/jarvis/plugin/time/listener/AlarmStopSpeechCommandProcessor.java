package com.gguproject.jarvis.plugin.time.listener;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.helper.sound.SoundPlayerService;
import com.gguproject.jarvis.plugin.display.event.DisplayEventData;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.listener.AsbtractSpeechCommandProcessor;
import com.gguproject.jarvis.plugin.time.listener.event.TimerDisplayData;

import javax.inject.Named;

@Named
public class AlarmStopSpeechCommandProcessor extends AsbtractSpeechCommandProcessor {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(AlarmStopSpeechCommandProcessor.class);

	private final SoundPlayerService soundPlayerService;

	private final EventBusService eventBusService;

	public AlarmStopSpeechCommandProcessor(SoundPlayerService soundPlayerService, EventBusService eventBusService) {
		super("ALARM", "STOP");
		this.soundPlayerService = soundPlayerService;
		this.eventBusService = eventBusService;
	}

	@Override
	public void process(DistributedEvent event, Command command) {
		LOGGER.debug("Stop alarm");
		this.soundPlayerService.stop();
		this.eventBusService.externalEmit(new DisplayEventData("alarm", TimerDisplayData.withHideStatus()));
	}
}
