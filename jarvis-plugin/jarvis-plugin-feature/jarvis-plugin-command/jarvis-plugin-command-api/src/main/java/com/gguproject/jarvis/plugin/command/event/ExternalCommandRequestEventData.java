package com.gguproject.jarvis.plugin.command.event;

import com.gguproject.jarvis.core.bus.support.EventData;

/**
 * Command event data
 *
 * @author guillaumegunther
 */
public class ExternalCommandRequestEventData extends EventData {
    private static final long serialVersionUID = -6841671964817624557L;
    public static final String eventType = "EXTERNAL_COMMAND_REQUEST";

    private String command;

    public ExternalCommandRequestEventData() {
        super(eventType, ExternalCommandRequestEventData.class);
    }

    public ExternalCommandRequestEventData(String command) {
        this();
        this.command = command;
    }

    @Override
    public String toString() {
        return "ExternalCommandRequestEventData";
    }

    public String getCommand() {
        return this.command;
    }
}
