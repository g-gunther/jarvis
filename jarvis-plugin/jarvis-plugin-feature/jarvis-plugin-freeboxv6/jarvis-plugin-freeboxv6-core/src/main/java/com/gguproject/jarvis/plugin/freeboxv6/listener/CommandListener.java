package com.gguproject.jarvis.plugin.freeboxv6.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.freeboxv6.FreeboxPluginConfiguration;
import com.gguproject.jarvis.plugin.freeboxv6.event.Freeboxv6Command;
import com.gguproject.jarvis.plugin.freeboxv6.event.Freeboxv6CommandEventData;
import com.gguproject.jarvis.plugin.freeboxv6.service.FreeboxRemoteService;
import com.gguproject.jarvis.plugin.freeboxv6.service.FreeboxServerService;
import com.gguproject.jarvis.plugin.freeboxv6.service.dto.PlayerStatus;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

/**
 * Service listener used to start the TV
 *
 * @author GGUNTHER
 */
@Named
public class CommandListener extends AbstractEventListener<Freeboxv6CommandEventData> {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(CommandListener.class);

    private final FreeboxServerService freeboxServerService;

    private final FreeboxRemoteService freeboxRemoteService;

    private final FreeboxPluginConfiguration configuration;

    private Map<Freeboxv6Command, Runnable> commandProcessors = new HashMap<>();

    public CommandListener(FreeboxServerService freeboxServerService, FreeboxRemoteService freeboxRemoteService,
                           FreeboxPluginConfiguration configuration, EventBusService eventBusService) {
        super(Freeboxv6CommandEventData.eventType, Freeboxv6CommandEventData.class, eventBusService);
        this.freeboxServerService = freeboxServerService;
        this.freeboxRemoteService = freeboxRemoteService;
        this.configuration = configuration;

        commandProcessors.put(Freeboxv6Command.POWER_ON, () -> {
            PlayerStatus status = freeboxServerService.playerStatus(this.configuration.getProperty(FreeboxPluginConfiguration.PropertyKey.Server.playerId));
            if (status != PlayerStatus.RUNNING) {
                freeboxRemoteService.power();
            }
        });
        commandProcessors.put(Freeboxv6Command.POWER_OFF, () -> {
            PlayerStatus status = freeboxServerService.playerStatus(this.configuration.getProperty(FreeboxPluginConfiguration.PropertyKey.Server.playerId));
            if (status != PlayerStatus.STANDBY) {
                freeboxRemoteService.power();
            }
        });
        commandProcessors.put(Freeboxv6Command.PLAY, () -> freeboxRemoteService.playPause());
        commandProcessors.put(Freeboxv6Command.PAUSE, () -> freeboxRemoteService.playPause());
    }

    @Override
    public void onEvent(DistributedEvent event, Freeboxv6CommandEventData data) {
        data.getCommands().forEach(command -> {
            commandProcessors.getOrDefault(command, () -> LOGGER.error("No command processor found for command: {}", command)).run();
        });
    }
}
