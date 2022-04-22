package com.gguproject.jarvis.plugin.command.event;

import com.gguproject.jarvis.core.bus.support.EventData;
import com.gguproject.jarvis.plugin.command.event.dto.CommandResponse;

import java.util.List;

/**
 * Command event data
 *
 * @author guillaumegunther
 */
public class ExternalCommandResponseEventData extends EventData {
    private static final long serialVersionUID = -6841671964817624557L;
    public static final String eventType = "EXTERNAL_COMMAND_RESPONSE";

    private String command;

    private List<CommandResponse> responses;

    public ExternalCommandResponseEventData() {
        super(eventType, ExternalCommandResponseEventData.class);
    }

    public ExternalCommandResponseEventData(String command, List<CommandResponse> responses) {
        this();
        this.command = command;
        this.responses = responses;
    }

    @Override
    public String toString() {
        return "ExternalCommandResponseEventData";
    }

    public String getCommand() {
        return this.command;
    }

    public List<CommandResponse> getResponses() {
        return this.responses;
    }
}
