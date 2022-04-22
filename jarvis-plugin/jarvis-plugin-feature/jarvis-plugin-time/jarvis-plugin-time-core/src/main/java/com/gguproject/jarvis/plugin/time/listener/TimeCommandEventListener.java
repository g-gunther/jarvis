package com.gguproject.jarvis.plugin.time.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.helper.sound.SoundPlayerService;
import com.gguproject.jarvis.plugin.time.event.TimeCommandEventData;
import com.gguproject.jarvis.plugin.time.event.TimeCommandEventData.TimeCommand;

import javax.inject.Named;

@Named
public class TimeCommandEventListener extends AbstractEventListener<TimeCommandEventData> {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(TimeCommandEventListener.class);

    private final SoundPlayerService soundPlayerService;

    public TimeCommandEventListener(SoundPlayerService soundPlayerService, EventBusService eventBusService) {
        super(TimeCommandEventData.eventType, TimeCommandEventData.class, eventBusService);
        this.soundPlayerService = soundPlayerService;
    }

    @Override
    public void onEvent(DistributedEvent event, TimeCommandEventData data) {
        if (data.getCommand() == TimeCommand.STOP) {
            LOGGER.debug("Stop alarm or timer sound");
            this.soundPlayerService.stop();
        }
    }
}
