package com.gguproject.jarvis.plugin.command.event.dto;

import java.util.Collections;
import java.util.List;

public class CommandResponse {
    public enum CommandResponseStatus {
        SUCCESS,
        ERROR,
        NOTFOUND;
    }

    /**
     * output status
     */
    private final CommandResponseStatus status;

    /**
     * Lines of text to display
     */
    private final List<String> lines;

    /**
     * Plugin name that generates the output
     */
    private String pluginName;

    /**
     * Plugin version that generates the output
     */
    private String pluginVersion;

    /**
     * Constructor
     *
     * @param lines Lines to display
     */
    public CommandResponse(CommandResponseStatus status, List<String> lines, String pluginName, String pluginVersion) {
        this.lines = lines;
        this.status = status;
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
    }

    /**
     * Get lines
     *
     * @return lines
     */
    public List<String> getLines() {
        return Collections.unmodifiableList(this.lines);
    }

    public String getPluginName() {
        return this.pluginName;
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }

    public CommandResponseStatus getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "CommandOutput [status=" + status + ", lines=" + lines + "]";
    }
}
