package com.gguproject.jarvis.plugin.command.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.events.api.tcp.TcpManager;
import com.gguproject.jarvis.core.events.tcp.support.TcpClientConnectionException;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.listener.event.CommandProcessorEventData;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.plugin.command.event.ExternalCommandRequestEventData;
import com.gguproject.jarvis.plugin.command.event.ExternalCommandResponseEventData;
import com.gguproject.jarvis.plugin.command.event.dto.CommandResponse;

import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Named
public class ExternalCommandRequestEventListener extends AbstractEventListener<ExternalCommandRequestEventData> {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(ExternalCommandRequestEventListener.class);

    private final TcpManager tcpManager;

    public ExternalCommandRequestEventListener(TcpManager tcpManager, EventBusService eventBusService) {
        super(ExternalCommandRequestEventData.eventType, ExternalCommandRequestEventData.class, eventBusService);
        this.tcpManager = tcpManager;
    }

    @Override
    public void onEvent(DistributedEvent event, ExternalCommandRequestEventData eventData) {
        List<Optional<CommandOutput>> commandResponses = this.eventBusService.emitAndWait(new CommandProcessorEventData(eventData.getCommand()), CommandOutput.class);
        LOGGER.debug("Received command ('{}') response: {}", eventData.getCommand(), commandResponses);
        try {
            this.tcpManager.answerToReceivedEvent(event, new ExternalCommandResponseEventData(eventData.getCommand(),
                    commandResponses.stream()
                            .filter(Optional::isPresent)
                            .map(o -> this.map(o.get()))
                            .collect(Collectors.toList()))
            );
        } catch (TcpClientConnectionException e) {
            LOGGER.error("Not able to send back command response", e);
        }
    }

    private CommandResponse map(CommandOutput output) {
        CommandResponse.CommandResponseStatus status;
        switch (output.getStatus()) {
            case ERROR:
                status = CommandResponse.CommandResponseStatus.ERROR;
                break;
            case SUCCESS:
                status = CommandResponse.CommandResponseStatus.SUCCESS;
                break;
            case NOTFOUND:
                status = CommandResponse.CommandResponseStatus.NOTFOUND;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + output.getStatus());
        }
        return new CommandResponse(status, output.getLines(), output.getPluginName(), output.getPluginVersion());
    }
}
