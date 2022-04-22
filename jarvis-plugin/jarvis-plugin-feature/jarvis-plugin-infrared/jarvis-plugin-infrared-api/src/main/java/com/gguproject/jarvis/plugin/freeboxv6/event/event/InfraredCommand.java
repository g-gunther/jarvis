package com.gguproject.jarvis.plugin.freeboxv6.event.event;

import java.util.Map;

public class InfraredCommand {

    private final String context;

    private final String action;

    private Map<String, String> properties;

    public InfraredCommand(String context, String action) {
        this.context = context;
        this.action = action;
    }

    public InfraredCommand(String context, String action, Map<String, String> properties) {
        this.context = context;
        this.action = action;
        this.properties = properties;
    }

    public String getContext() {
        return context;
    }

    public String getAction() {
        return action;
    }

    public boolean hasProperty(String key) {
        return this.properties != null && this.properties.containsKey(key);
    }

    public String getProperty(String key) {
        return this.properties.get(key);
    }

    @Override
    public String toString() {
        return "InfraredCommand [context=" + context + ", action=" + action + ", properties=" + properties + "]";
    }
}
