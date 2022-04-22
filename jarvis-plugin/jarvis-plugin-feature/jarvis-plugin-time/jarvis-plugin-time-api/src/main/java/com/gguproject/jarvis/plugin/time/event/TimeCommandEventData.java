package com.gguproject.jarvis.plugin.time.event;

import com.gguproject.jarvis.core.bus.support.EventData;

/**
 * Command event data
 *
 * @author guillaumegunther
 */
public class TimeCommandEventData extends EventData {
    private static final long serialVersionUID = -6841671964817624557L;
    public static final String eventType = "TIME_COMMAND";

    private TimeCommandTarget target;

    private TimeCommand command;

    public TimeCommandEventData() {
        super(eventType, TimeCommandEventData.class);
    }

    @Override
    public String toString() {
        return "TimeEventData";
    }

    public enum TimeCommandTarget {
        ALARM,
        TIMER;
    }

    public enum TimeCommand {
        STOP;
    }

    public TimeCommandTarget getTarget() {
        return target;
    }

    public TimeCommand getCommand() {
        return command;
    }
}
