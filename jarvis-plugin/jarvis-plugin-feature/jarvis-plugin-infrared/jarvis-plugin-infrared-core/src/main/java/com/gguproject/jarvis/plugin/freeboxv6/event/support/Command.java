package com.gguproject.jarvis.plugin.freeboxv6.event.support;

public class Command {

    private String context;

    private String action;

    private String command;

    public Command() {
    }

    public boolean match(String context, String action) {
        return this.context.equals(context) && this.action.equals(action);
    }

    public String getCommand() {
        return this.command;
    }

    @Override
    public String toString() {
        return "Command [context=" + context + ", action=" + action + ", command=" + command + "]";
    }
}
