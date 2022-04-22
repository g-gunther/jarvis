package com.gguproject.jarvis.plugin.time.listener;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.helper.sound.SoundPlayerService;
import com.gguproject.jarvis.plugin.display.event.DisplayEventData;
import com.gguproject.jarvis.plugin.speaker.event.SpeakerEventData;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.listener.AsbtractSpeechCommandProcessor;
import com.gguproject.jarvis.plugin.time.TimePluginConfiguration;
import com.gguproject.jarvis.plugin.time.TimePluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.time.listener.event.TimerDisplayData;
import com.gguproject.jarvis.plugin.time.service.DelayService;
import com.gguproject.jarvis.plugin.time.service.SpeakerService;
import com.gguproject.jarvis.plugin.time.service.SpeakerService.TimerType;

import javax.inject.Named;
import java.util.Date;

@Named
public class AlarmStartSpeechCommandProcessor extends AsbtractSpeechCommandProcessor {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(AlarmStartSpeechCommandProcessor.class);

	private final TimePluginConfiguration configuration;

	private final SoundPlayerService soundPlayerService;

	private final SpeakerService speakerService;

	private final DelayService delayService;

	private final EventBusService eventBusService;

	public AlarmStartSpeechCommandProcessor(TimePluginConfiguration configuration, SoundPlayerService soundPlayerService,
											SpeakerService speakerService, DelayService delayService,
											EventBusService eventBusService) {
		super("ALARM", "SET");
		this.configuration = configuration;
		this.soundPlayerService = soundPlayerService;
		this.speakerService = speakerService;
		this.delayService = delayService;
		this.eventBusService = eventBusService;
	}

	@Override
	public void process(DistributedEvent event, Command command) {
		LOGGER.debug("Start alarm");

		if(!command.getTime().isTimeDefined()){
			LOGGER.info("No time defined - can not set an alarm");
			return;
		}

		this.delayService.delay(() -> {
			this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.soundAlarm))
					.ifPresentOrElse(
							file -> this.soundPlayerService.playLoop(file),
							() -> LOGGER.error("No sound file found for alarm")
					);
		}, new Date(command.getTime().toOffsetDateTime().toInstant().toEpochMilli()));

		String text = this.speakerService.toSpeech(TimerType.ALARM, command.getTime());
		LOGGER.debug("Speak data {}", text);
		this.eventBusService.externalEmit(SpeakerEventData.withSpeech(text));
		this.eventBusService.externalEmit(new DisplayEventData("alarm", TimerDisplayData.withTime(command.getTime().toOffsetDateTime())));
	}
}
