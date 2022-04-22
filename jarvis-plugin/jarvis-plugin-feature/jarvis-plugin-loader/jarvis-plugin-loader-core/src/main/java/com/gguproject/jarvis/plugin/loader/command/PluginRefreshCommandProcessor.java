package com.gguproject.jarvis.plugin.loader.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.loader.command.utils.CommandOutputUtils;
import com.gguproject.jarvis.plugin.loader.jarloader.PluginJarService;
import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarReport;

import javax.inject.Named;

@Named
@Qualifier(PluginCommandProcessor.qualifier)
public class PluginRefreshCommandProcessor extends AbstractCommandProcessor {

    private final PluginJarService jarUpdaterService;

    public PluginRefreshCommandProcessor(PluginJarService jarUpdaterService) {
        super("refresh", "refresh single or all plugins");
        this.registerArgumentDescription("available", "Refresh only already loaded jars");
        this.registerArgumentDescription("all", " Refresh the entire configuration. Remove unsed jars files, load the new one and refresh the others");
        this.registerArgumentDescription("name=<name>", "Refresh a given jar by its name. If doesn't exist, download it");
        this.jarUpdaterService = jarUpdaterService;
    }

    @Override
    public CommandOutput process(CommandRequest command) {
        PluginJarReport report;


        //--> add reload argument


        /*
         * Refresh all available plugins
         */
        if (command.hasArgument("available")) {
            report = this.jarUpdaterService.refreshAll();
        }
        /*
         * Refresh all plugins
         */
        else if (command.hasArgument("all")) {
            report = this.jarUpdaterService.process();
        }
        /*
         * Refresh a given plugin
         */
        else if (command.hasArgument("name")) {
            report = this.jarUpdaterService.refresh(command.getArgument("name"), command.hasArgument("force"));
        }
        // error
        else {
            return CommandOutputBuilder.build("Not able to process command: {0}", command.getCommand()).get();
        }

        return CommandOutputUtils.build(report);
    }
}