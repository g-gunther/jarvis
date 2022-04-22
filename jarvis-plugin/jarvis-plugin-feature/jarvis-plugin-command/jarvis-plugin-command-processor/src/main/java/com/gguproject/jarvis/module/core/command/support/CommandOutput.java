package com.gguproject.jarvis.module.core.command.support;

import java.util.Collections;
import java.util.List;

/**
 * Object containing the output of a command processor
 */
public class CommandOutput {
    
    public enum CommandOutputStatus {
        SUCCESS,
        ERROR,
        NOTFOUND;
    }

    /**
     * output status
     */
    private final CommandOutputStatus status;

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
    public CommandOutput(CommandOutputStatus status, List<String> lines) {
        this.lines = lines;
        this.status = status;
    }

    /**
     * Constructor
     *
     * @param lines Lines to display
     */
    public CommandOutput(List<String> lines) {
        this.lines = lines;
        this.status = CommandOutputStatus.SUCCESS;
    }

    public CommandOutput withPlugin(String pluginName, String pluginVersion) {
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
        return this;
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

    public CommandOutputStatus getStatus() {
        return this.status;
    }

    public boolean isSuccess() {
        return this.status == CommandOutputStatus.SUCCESS;
    }

    public boolean isError() {
        return this.status == CommandOutputStatus.ERROR;
    }

    public boolean isNotFound() {
        return this.status == CommandOutputStatus.NOTFOUND;
    }

    @Override
    public String toString() {
        return "CommandOutput [status=" + status + ", lines=" + lines + "]";
    }
}
