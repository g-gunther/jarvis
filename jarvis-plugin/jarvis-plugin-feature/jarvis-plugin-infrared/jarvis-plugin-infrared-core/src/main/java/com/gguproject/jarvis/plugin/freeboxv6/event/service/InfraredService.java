package com.gguproject.jarvis.plugin.freeboxv6.event.service;

import com.gguproject.jarvis.helper.shell.ShellCommandException;
import com.gguproject.jarvis.plugin.freeboxv6.event.analyser.CommandAnalyser;
import com.gguproject.jarvis.plugin.freeboxv6.event.analyser.processor.CommandProcessor;
import com.gguproject.jarvis.plugin.freeboxv6.event.event.InfraredCommand;

import javax.inject.Named;
import java.util.List;

@Named
public class InfraredService {

    private final CommandAnalyser commandAnalyser;

    public InfraredService(CommandAnalyser commandAnalyser){
        this.commandAnalyser = commandAnalyser;
    }

    public boolean process(List<InfraredCommand> commands) throws ShellCommandException {
        List<CommandProcessor> commandProcessors = this.commandAnalyser.analyse(commands);

        if (!commands.isEmpty()) {
            for (CommandProcessor processor : commandProcessors) {
                processor.process();
            }
            return true;
        }
        return false;
    }
}
