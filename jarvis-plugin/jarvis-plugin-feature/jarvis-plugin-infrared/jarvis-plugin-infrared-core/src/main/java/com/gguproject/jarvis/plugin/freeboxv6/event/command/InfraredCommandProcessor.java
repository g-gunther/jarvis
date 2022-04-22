package com.gguproject.jarvis.plugin.freeboxv6.event.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.freeboxv6.event.event.InfraredCommand;
import com.gguproject.jarvis.plugin.freeboxv6.event.service.InfraredService;

import javax.inject.Named;
import java.util.Arrays;

@Named
@Qualifier(AbstractCommandProcessor.rootQualifier)
public class InfraredCommandProcessor extends AbstractCommandProcessor {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(InfraredCommandProcessor.class);

    private final InfraredService infraresService;

    public InfraredCommandProcessor(InfraredService infraresService) {
        super("infrared", "All infrared related commands");
        this.infraresService = infraresService;
    }

    @Override
    public CommandOutput process(CommandRequest command) {
        if (!command.hasArgument("context") || !command.hasArgument("action")) {
            return CommandOutputBuilder.build("No context or action argument found").get();
        }

        if (this.infraresService.process(Arrays.asList(new InfraredCommand(command.getArgument("context"), command.getArgument("action"))))) {
            return CommandOutputBuilder.build("Command executed").get();
        }
        return CommandOutputBuilder.build("No command found to execute").get();
    }
}
