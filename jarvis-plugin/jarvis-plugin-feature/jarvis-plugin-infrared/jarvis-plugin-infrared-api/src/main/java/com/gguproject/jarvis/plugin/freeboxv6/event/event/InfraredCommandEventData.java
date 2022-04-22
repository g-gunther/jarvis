package com.gguproject.jarvis.plugin.freeboxv6.event.event;

import com.gguproject.jarvis.core.bus.support.EventData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Command event data
 *
 * @author guillaumegunther
 */
public class InfraredCommandEventData extends EventData {
    private static final long serialVersionUID = -6841671964817624557L;
    public static final String eventType = "INFRARED_COMMAND";

    /**
     * Context of the event
     */
    private List<InfraredCommand> commands = new ArrayList<>();

    public InfraredCommandEventData() {
        super(eventType, InfraredCommandEventData.class);
    }

    /**
     * Constructor
     *
     * @param contextType     Event context
     * @param eventActionType Action
     */
    public InfraredCommandEventData(InfraredCommand... commands) {
        this();
        this.commands = Arrays.asList(commands);
    }


    public List<InfraredCommand> getCommands() {
        return this.commands;
    }

    public static InfraredCommandEventData get() {
        return new InfraredCommandEventData();
    }

    public InfraredCommandEventData add(String context, String action) {
        this.commands.add(new InfraredCommand(context, action));
        return this;
    }

    public InfraredCommandEventData add(String context, String action, Map<String, String> properties) {
        this.commands.add(new InfraredCommand(context, action, properties));
        return this;
    }

    @Override
    public String toString() {
        return "CommandEventData [commands=" + this.commands + "]";
    }
}
