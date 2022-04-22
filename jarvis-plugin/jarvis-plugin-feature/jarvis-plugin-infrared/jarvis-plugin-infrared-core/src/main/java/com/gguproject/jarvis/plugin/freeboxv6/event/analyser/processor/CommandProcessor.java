package com.gguproject.jarvis.plugin.freeboxv6.event.analyser.processor;

import com.gguproject.jarvis.helper.shell.ShellCommandException;

/**
 * Interface that defines a command processor
 * A command processor is used to process some data and returns some shell commands to execute
 * on the server
 */
public interface CommandProcessor {

    public void process() throws ShellCommandException;
}
