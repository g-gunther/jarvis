package com.gguproject.jarvis.plugin.freeboxv6.event.analyser.processor;

import com.gguproject.jarvis.plugin.freeboxv6.event.event.InfraredCommand;

/**
 * Build a {@link WaitCommandProcessor} to wait before processing the command
 */
public class WaitCommandProcessorFactory implements CommandProcessorFactory<CommandProcessor> {

    private static final int defaultDuration = 1000;

    private static final String context = "GENERAL";

    private static final String action = "SLEEP";

    @Override
    public WaitCommandProcessor build(InfraredCommand command) {
        if (command.hasProperty("duration")) {
            return new WaitCommandProcessor(Integer.valueOf(command.getProperty("duration")));
        }
        return new WaitCommandProcessor(defaultDuration);
    }

    @Override
    public boolean match(String context, String action) {
        return WaitCommandProcessorFactory.context.equals(context) && WaitCommandProcessorFactory.action.equals(action);
    }
}
