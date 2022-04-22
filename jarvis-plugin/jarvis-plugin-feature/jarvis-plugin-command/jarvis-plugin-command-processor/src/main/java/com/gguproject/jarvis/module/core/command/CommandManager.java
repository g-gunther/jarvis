package com.gguproject.jarvis.module.core.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.utils.StringUtils;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;

import javax.inject.Named;
import java.util.List;

/**
 * Root of all commands processor
 * This is the main entry point to find all other {@link AbstractCommandProcessor}
 */
@Named
public class CommandManager extends AbstractParentCommandProcessor {

    /**
     * Root constructor
     */
    public CommandManager(@Qualifier(AbstractCommandProcessor.rootQualifier) List<AbstractCommandProcessor> processors) {
        super("root", "root", processors);
    }

    /**
     * Process a command
     *
     * @param command
     * @return
     */
    public CommandOutput process(String command) {
        return this.process(CommandRequestFactory.parse(command));
    }

    /**
     * Process the {@link CommandRequest}
     */
    public CommandOutput process(CommandRequest command) {
        if (!StringUtils.isEmpty(command.getCommand())) {
            return super.process(command);
        }

        return CommandOutputBuilder.build("Empty command value").get();
    }
}
