package com.gguproject.jarvis.module.core.command.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.CommandManager;
import com.gguproject.jarvis.module.core.command.CommandRequestFactory;
import com.gguproject.jarvis.module.core.command.listener.event.CommandProcessorEventData;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.plugin.core.ClassLoaderContext;

import javax.inject.Named;
import java.util.Optional;

/**
 * Command event listener
 * Listens to {@link CommandProcessorEventData} and process the received command
 * using the {@link CommandManager} root.
 * It then send back the {@link CommandOutput} to the requester
 * This listener will be loaded in each plugins / modules having commands to process
 */
@Named
public class CommandProcessorListener extends AbstractEventListener<CommandProcessorEventData> {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(CommandProcessorListener.class);

    /**
     * Root of command processors
     */
    private final CommandManager commandManager;

    public CommandProcessorListener(CommandManager commandManager, EventBusService eventBusService) {
        super(CommandProcessorEventData.eventType, CommandProcessorEventData.class, eventBusService);
        this.commandManager= commandManager;
    }

    @Override
    public Optional<Object> onEventWithResponse(DistributedEvent event, CommandProcessorEventData data) {
        LOGGER.info("Process command: {}", data.getCommand());
        CommandOutput output;
        try {
            output = this.commandManager.process(CommandRequestFactory.parse(data.getCommand()));

            LOGGER.debug("Command output: {}", output);
        } catch (Exception e) {
            LOGGER.error("An error occurs while processing command: {}", data.getCommand(), e);
            output = CommandOutputBuilder.build("Error occurs while processing command {0}: {1}", data.getCommand(), e.getMessage()).error().get();
        }

        return Optional.of(output.withPlugin(ClassLoaderContext.getName(), ClassLoaderContext.getVersion()));
    }
}
