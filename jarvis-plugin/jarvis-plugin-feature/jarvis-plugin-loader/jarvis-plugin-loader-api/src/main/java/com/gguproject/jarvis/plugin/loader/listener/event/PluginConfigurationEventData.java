package com.gguproject.jarvis.plugin.loader.listener.event;

import com.gguproject.jarvis.core.bus.support.EventData;

public class PluginConfigurationEventData extends EventData {
    private static final long serialVersionUID = -6841671964817624557L;
    private static final String eventType = "PLUGIN_CONFIGURATION";

    private Action action;

    private String pluginName;

    public PluginConfigurationEventData() {
        super(eventType, PluginConfigurationEventData.class);
    }

    public PluginConfigurationEventData(Action action, String pluginName) {
        this();
        this.action = action;
        this.pluginName = pluginName;
    }

    public Action getAction() {
        return action;
    }

    public String getPluginName() {
        return pluginName;
    }

    public enum Action {
        ADD,
        REMOVE;
    }
}
