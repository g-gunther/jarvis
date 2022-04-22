package com.gguproject.jarvis.plugin.freeboxv6.event;

import com.gguproject.jarvis.core.bus.support.EventData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command event data
 *
 * @author guillaumegunther
 */
public class Freeboxv6CommandEventData extends EventData {
    private static final long serialVersionUID = -6841671964817624557L;
    public static final String eventType = "FREEBOXV6_COMMAND";

    /**
     * Context of the event
     */
    private List<Freeboxv6Command> commands = new ArrayList<>();

    public Freeboxv6CommandEventData() {
        super(eventType, Freeboxv6CommandEventData.class);
    }

    /**
     * Constructor
     *
     * @param contextType     Event context
     * @param eventActionType Action
     */
    public Freeboxv6CommandEventData(Freeboxv6Command... commands) {
        this();
        this.commands = Arrays.asList(commands);
    }


    public List<Freeboxv6Command> getCommands() {
        return this.commands;
    }

    public static Freeboxv6CommandEventData get() {
        return new Freeboxv6CommandEventData();
    }

    public Freeboxv6CommandEventData add(Freeboxv6Command command) {
        this.commands.add(command);
        return this;
    }

    @Override
    public String toString() {
        return "Freeboxv6CommandEventData [commands=" + this.commands + "]";
    }
}
