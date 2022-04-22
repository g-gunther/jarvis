package com.gguproject.jarvis.module.core.command.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Command to execute
 * A command is basically like a console or shell command
 * which will be processed by AbstractCommandProcessor in modules and plugins
 */
public class CommandRequest {
    /**
     * Command to execute
     */
    private String command;

    /**
     * Iterator over all words of the command
     */
    private Iterator<String> commandIterator;

    /**
     * List of found arguments
     */
    private Map<String, String> arguments = new HashMap<>();

    public CommandRequest(String command, List<String> commandParts, Map<String, String> arguments) {
        this.command = command;
        this.commandIterator = commandParts.listIterator();
        this.arguments = arguments;
    }

    /**
     * Get the command
     *
     * @return Command
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * Return the iterator over all command words
     *
     * @return
     */
    public Iterator<String> getIterator() {
        return this.commandIterator;
    }

    /**
     * Return the list of arguments
     *
     * @return Arguments
     */
    public Map<String, String> getArguments() {
        return this.arguments;
    }

    /**
     * Check if there is a given argument
     *
     * @param key Argument name
     * @return True if exists, false else
     */
    public boolean hasArgument(String key) {
        return this.arguments.containsKey(key);
    }

    /**
     * Get the argument value
     *
     * @param key Argument name
     * @return Value of the argument null else
     */
    public String getArgument(String key) {
        return this.arguments.get(key);
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public String getArgumentOrElse(String key, String defaultValue) {
        return this.arguments.getOrDefault(key, defaultValue);
    }

    public Integer getIntArgument(String key) {
        return Integer.valueOf(this.arguments.get(key));
    }

    public Integer getIntArgumentOrElse(String key, Integer defaultValue) {
        if (this.arguments.containsKey(key)) {
            return Integer.valueOf(this.arguments.get(key));
        }
        return defaultValue;
    }

    public Boolean getBoolArgument(String key) {
        return Boolean.valueOf(this.arguments.get(key));
    }

    public Boolean getBoolArgumentOrElse(String key, Boolean defaultValue) {
        if (this.arguments.containsKey(key)) {
            return Boolean.valueOf(this.arguments.get(key));
        }
        return defaultValue;
    }

    @Override
    public String toString() {
        return "CommandRequest [command=" + command + ", arguments=" + arguments + "]";
    }
}
