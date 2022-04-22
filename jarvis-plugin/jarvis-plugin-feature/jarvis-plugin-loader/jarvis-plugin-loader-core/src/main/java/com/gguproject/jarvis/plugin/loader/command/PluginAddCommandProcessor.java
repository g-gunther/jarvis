package com.gguproject.jarvis.plugin.loader.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.loader.command.utils.CommandOutputUtils;
import com.gguproject.jarvis.plugin.loader.jarloader.PluginJarLoader;
import com.gguproject.jarvis.plugin.loader.jarloader.PluginJarService;
import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarReport;

import javax.inject.Named;

@Named
@Qualifier(PluginCommandProcessor.qualifier)
public class PluginAddCommandProcessor extends AbstractCommandProcessor {

    private final PluginJarService jarUpdaterService;

    private final PluginJarLoader pluginJarLoader;

    public PluginAddCommandProcessor(PluginJarService jarUpdaterService, PluginJarLoader pluginJarLoader) {
        super("add", "Add a plugin to the configuration");
        this.jarUpdaterService = jarUpdaterService;
        this.pluginJarLoader = pluginJarLoader;
    }

    @Override
    public CommandOutput process(CommandRequest command) {
        if (!command.hasArgument("name")) {
            return CommandOutputBuilder.build("There is no 'name' arguments in command: ''{0}''", command.getCommand()).get();
        }
        String jarName = command.getArgument("name");

        // try to add the plugin in configuration file & download the last version
        PluginJarReport report = this.jarUpdaterService.add(jarName);
        if (command.hasArgument("load") && report.getCounter(PluginJarReport.Counter.DOWNLOAD) == 1) {
            // the jar has been downloaded, try to start it if necessary
            if (this.pluginJarLoader.load(this.jarUpdaterService.getContext(jarName))) {
                report.incrementCounter(PluginJarReport.Counter.LOADED);
            }
        }

        return CommandOutputUtils.build(report);
    }
}