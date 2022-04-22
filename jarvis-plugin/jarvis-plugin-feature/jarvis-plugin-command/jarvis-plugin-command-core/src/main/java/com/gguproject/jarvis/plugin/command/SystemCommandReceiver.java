package com.gguproject.jarvis.plugin.command;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.daemon.DaemonThread;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.StringUtils;
import com.gguproject.jarvis.module.core.command.listener.event.CommandProcessorEventData;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;

import javax.inject.Named;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

/**
 * Thread permettant d'ecouter une source de commande (console) et de les propager dans le
 * bus de message
 */
@Named
public class SystemCommandReceiver extends DaemonThread {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SystemCommandReceiver.class);

    private BufferedReader inputReader;

    private final EventBusService eventBusService;

    public SystemCommandReceiver(EventBusService eventBusService) {
        super("COMMAND");
        this.eventBusService = eventBusService;

        InputStreamReader in = new InputStreamReader(System.in);
        inputReader = new BufferedReader(in);
    }

    @Override
    public void run() {
        try {
            String command;
            while ((command = this.inputReader.readLine()) != null) {
                if (!StringUtils.isEmpty(command)) {
                    LOGGER.debug("Send command: {}", command);
                    // emit the command to process internally so that every plugin can process it
                    List<Optional<CommandOutput>> commandResponses = this.eventBusService.emitAndWait(new CommandProcessorEventData(command), CommandOutput.class);
                    LOGGER.info("----------------");
                    LOGGER.info("Command: {}", command);
                    commandResponses.stream()
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(CommandOutput::isSuccess)
                        .filter(o -> !o.getLines().isEmpty()).forEach(r -> {
                            LOGGER.info("- {}:{}", r.getPluginName(), r.getPluginVersion());
                            r.getLines().forEach(LOGGER::info);
                    });
                    LOGGER.info("----------------");
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error while listning to the stsme input stream", e);
        }
    }
}
