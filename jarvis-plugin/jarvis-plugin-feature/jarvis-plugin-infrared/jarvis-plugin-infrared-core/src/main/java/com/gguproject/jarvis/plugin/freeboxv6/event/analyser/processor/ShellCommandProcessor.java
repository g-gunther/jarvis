package com.gguproject.jarvis.plugin.freeboxv6.event.analyser.processor;

import com.gguproject.jarvis.helper.shell.AbstractShellService;
import com.gguproject.jarvis.helper.shell.ShellCommandException;

/**
 * Simple command processor
 */
public class ShellCommandProcessor extends AbstractShellService implements CommandProcessor {

    private String command;

    public ShellCommandProcessor(String command) {
        this.command = command;
    }

    @Override
    public void process() throws ShellCommandException {
        this.exec(this.command);
    }

    @Override
    public String toString() {
        return "ShellCommandProcessor [command=" + command + "]";
    }
}
