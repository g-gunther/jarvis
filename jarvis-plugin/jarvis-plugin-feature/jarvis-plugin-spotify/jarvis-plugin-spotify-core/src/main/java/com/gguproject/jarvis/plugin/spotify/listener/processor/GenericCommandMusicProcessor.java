package com.gguproject.jarvis.plugin.spotify.listener.processor;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.freeboxv6.event.event.InfraredCommandEventData;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.listener.AsbtractSpeechCommandProcessor;
import com.gguproject.jarvis.plugin.spotify.SpotifyPluginConfiguration;
import com.gguproject.jarvis.plugin.spotify.SpotifyPluginConfiguration.PropertyKey;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import javax.inject.Named;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Named
public class GenericCommandMusicProcessor extends AsbtractSpeechCommandProcessor implements OnPostConstruct {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(GenericCommandMusicProcessor.class);

    private final EventBusService eventBusService;

    private final SpotifyPluginConfiguration configuration;

    private List<InfraredCommand> infraredCommands;

    public GenericCommandMusicProcessor(EventBusService eventBusService, SpotifyPluginConfiguration configuration) {
        super("MUSIC", "MUTE", "UNMUTE", "STOP", "START", "VOLUME_DOWN", "VOLUME_UP");
        this.eventBusService = eventBusService;
        this.configuration = configuration;
    }

    @Override
    public void postConstruct() {
        Optional<File> file = this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.infraredCommandsConfigurationFile));

        if (file.isEmpty()) {
            LOGGER.info("No command configuration file found - exit");
            return;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();

        JsonReader reader;
        try {
            reader = new JsonReader(new FileReader(file.get()));
            Type listType = new TypeToken<ArrayList<InfraredCommand>>() {
            }.getType();
            this.infraredCommands = gsonBuilder.create().fromJson(reader, listType);
            LOGGER.debug("Load infrared commands from configuration file: {}", this.infraredCommands);
        } catch (FileNotFoundException e) {
            LOGGER.error("Can't load the given infrared command file: {}", file.get().getName(), e);
            throw new IllegalStateException("Not able to load the infrared command file " + file.get().getAbsolutePath());
        }
    }

    @Override
    public void process(DistributedEvent event, Command command) {
        this.infraredCommands.stream().filter(c -> c.getActions().contains(command.getAction()))
                .findFirst()
                .ifPresent(infraredCommand -> {
                    LOGGER.debug("Infrared command found for context {} and action {}", command.getContext(), command.getAction());
                    InfraredCommandEventData infraredEvent = InfraredCommandEventData.get();
                    infraredCommand.getCommands().forEach(c -> {
                        infraredEvent.add(c.getContext(), c.getAction(), c.getProperties());
                    });
                    this.eventBusService.externalEmit(infraredEvent);
                });
    }
}
