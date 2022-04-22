package com.gguproject.jarvis.plugin.tv.listener.processor;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.bus.support.EventData;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.freeboxv6.event.Freeboxv6Command;
import com.gguproject.jarvis.plugin.freeboxv6.event.Freeboxv6CommandEventData;
import com.gguproject.jarvis.plugin.freeboxv6.event.event.InfraredCommandEventData;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.listener.AsbtractSpeechCommandProcessor;
import com.gguproject.jarvis.plugin.tv.TvPluginConfiguration;
import com.gguproject.jarvis.plugin.tv.TvPluginConfiguration.PropertyKey;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import javax.inject.Named;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

@Named
public class GenericCommandTvProcessor extends AsbtractSpeechCommandProcessor implements OnPostConstruct {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(GenericCommandTvProcessor.class);

    private final EventBusService eventBusService;

    private final TvPluginConfiguration configuration;

    private List<CommandConfiguration> commandConfigurations;

    private Map<CommandConfiguration.CommandType, Function<List<CommandConfiguration.Event>, EventData>> commandTypeEventProcessors = new HashMap<>();

    public GenericCommandTvProcessor(EventBusService eventBusService, TvPluginConfiguration configuration) {
        super("TV", "MUTE", "UNMUTE", "STOP", "START", "VOLUME_DOWN", "VOLUME_UP");
        this.eventBusService = eventBusService;
        this.configuration = configuration;
    }

    @Override
    public void postConstruct() {
        Optional<File> file = this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.commandsConfigurationFile));
        if (file.isEmpty()) {
            LOGGER.info("No command configuration file found - exit");
            return;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();

        JsonReader reader;
        try {
            reader = new JsonReader(new FileReader(file.get()));
            Type listType = new TypeToken<ArrayList<CommandConfiguration>>() {
            }.getType();
            this.commandConfigurations = gsonBuilder.create().fromJson(reader, listType);
            LOGGER.debug("Load infrared commands from configuration file: {}", this.commandConfigurations);
        } catch (FileNotFoundException e) {
            LOGGER.error("Can't load the given infrared command file: {}", file.get().getName(), e);
            throw new IllegalStateException("Not able to load the infrared command file " + file.get().getAbsolutePath());
        }

        /*
         * Generate infrared event
         */
        commandTypeEventProcessors.put(CommandConfiguration.CommandType.INFRARED, (events) -> {
            InfraredCommandEventData event = InfraredCommandEventData.get();
            events.forEach(e -> {
                event.add(e.getContext(), e.getAction(), e.getProperties());
            });
            return event;
        });

        /*
         * Generate freebox v6 event
         */
        commandTypeEventProcessors.put(CommandConfiguration.CommandType.FREEBOXV6, (events) -> {
            Freeboxv6CommandEventData event = Freeboxv6CommandEventData.get();
            events.forEach(e -> {
                event.add(Freeboxv6Command.valueOf(e.getAction()));
            });
            return event;
        });
    }

    @Override
    public void process(DistributedEvent event, Command command) {
        this.commandConfigurations.stream().filter(c -> c.getActions().contains(command.getAction()))
                .findFirst()
                .ifPresent(commandConfiguration -> {
                    LOGGER.debug("Command configuration found for action {}", command.getAction());
                    commandConfiguration.getCommands().forEach(c -> {
                        this.eventBusService.externalEmit(commandTypeEventProcessors.get(c.getType()).apply(c.getEvents()));
                    });
                });
    }
}
