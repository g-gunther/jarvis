package com.gguproject.jarvis.plugin.spotify.listener;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.StringUtils;
import com.gguproject.jarvis.plugin.freeboxv6.event.event.InfraredCommandEventData;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.GrammarWord;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.listener.AsbtractSpeechCommandProcessor;
import com.gguproject.jarvis.plugin.spotify.service.DeviceConfigurationService;
import com.gguproject.jarvis.plugin.spotify.service.SpotifyService;
import com.gguproject.jarvis.plugin.spotify.service.SpotifyServiceOrchestrator;
import com.gguproject.jarvis.plugin.spotify.service.dto.PlaylistDto;

import javax.inject.Named;
import java.util.Optional;

/**
 * Start a playlist on the registered device
 */
@Named
public class SetMusicListener extends AsbtractSpeechCommandProcessor {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SetMusicListener.class);

    private final SpotifyService spotifyService;

    private final SpotifyServiceOrchestrator spotifyServiceOrchestrator;

    private final DeviceConfigurationService deviceConfigurationService;

    private final EventBusService eventBusService;

    public SetMusicListener(SpotifyService spotifyService, SpotifyServiceOrchestrator spotifyServiceOrchestrator,
                            DeviceConfigurationService deviceConfigurationService, EventBusService eventBusService) {
        super("MUSIC", "SET");
        this.spotifyService = spotifyService;
        this.spotifyServiceOrchestrator = spotifyServiceOrchestrator;
        this.deviceConfigurationService = deviceConfigurationService;
        this.eventBusService = eventBusService;
    }

    @Override
    public void process(DistributedEvent event, Command command) {
        LOGGER.info("Try to set a playlist");

        // some data has been sent which might match a playlist
        if (!StringUtils.isEmpty(command.getData())) {
            GrammarWord dataWord = GrammarWord.build(command.getData());
            Optional<PlaylistDto> playlist = this.spotifyService.getCurrentUserPlaylists().stream()
                    .filter(p -> dataWord.match(p.getName())).findFirst();

            if (playlist.isEmpty()) {
                LOGGER.error("No playlist found for :{}", command.getData());
                return;
            }

            this.spotifyServiceOrchestrator.playPlaylist(playlist.get().getId());

        } else {
            this.spotifyServiceOrchestrator.play();
        }

        // send an event to select the analog channel
        this.eventBusService.externalEmit(InfraredCommandEventData.get().add("MUSIC", "SOURCE_ANALOG"));
    }
}
