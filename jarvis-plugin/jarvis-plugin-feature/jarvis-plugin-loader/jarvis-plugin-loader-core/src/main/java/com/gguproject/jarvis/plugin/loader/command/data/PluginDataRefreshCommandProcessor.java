package com.gguproject.jarvis.plugin.loader.command.data;

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
@Qualifier(PluginDataCommandProcessor.qualifier)
public class PluginDataRefreshCommandProcessor extends AbstractCommandProcessor {

    private final PluginJarService jarUpdaterService;

    public PluginDataRefreshCommandProcessor(PluginJarService jarUpdaterService) {
        super("refresh", "refresh data of single or all plugins");
        this.registerArgumentDescription("available", "Refresh only data of already loaded jars");
        this.registerArgumentDescription("name=<name>", "Refresh a given data jar by its name. If doesn't exist, download it");
        this.jarUpdaterService = jarUpdaterService;
    }

    @Override
    public CommandOutput process(CommandRequest command) {
        PluginJarReport report;

        /*
         * Refresh a given plugin
         */
        if (command.hasArgument("name")) {
            report = this.jarUpdaterService.refreshData(command.getArgument("name"), command.hasArgument("force"));
        }
        // error
        else {
            return CommandOutputBuilder.build("Not able to process command: {}", command.getCommand()).get();
        }

        return CommandOutputUtils.build(report);
    }
}
