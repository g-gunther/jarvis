package com.gguproject.jarvis.plugin.freeboxv6.event.analyser.processor;

import com.gguproject.jarvis.plugin.freeboxv6.event.event.InfraredCommand;

public class ShellCommandProcessorFactory implements CommandProcessorFactory<ShellCommandProcessor> {

    private String context;

    private String action;

    private String command;

    public ShellCommandProcessorFactory(String context, String action, String command) {
        this.context = context;
        this.action = action;
        this.command = command;
    }

    @Override
    public boolean match(String context, String action) {
        return this.context.equals(context) && this.action.equals(action);
    }

    @Override
    public ShellCommandProcessor build(InfraredCommand command) {
        return new ShellCommandProcessor(this.command);
    }
}
