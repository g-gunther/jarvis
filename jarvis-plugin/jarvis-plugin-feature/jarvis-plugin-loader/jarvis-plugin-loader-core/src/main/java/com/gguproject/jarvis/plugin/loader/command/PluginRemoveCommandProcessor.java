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
public class PluginRemoveCommandProcessor extends AbstractCommandProcessor {

    private final PluginJarService jarUpdaterService;

    private final PluginJarLoader pluginJarLoader;

    public PluginRemoveCommandProcessor(PluginJarService jarUpdaterService, PluginJarLoader pluginJarLoader) {
        super("rm", "Remove a plugin to configuration");
        this.jarUpdaterService = jarUpdaterService;
        this.pluginJarLoader = pluginJarLoader;
    }

    @Override
    public CommandOutput process(CommandRequest command) {
        if (!command.hasArgument("name")) {
            return CommandOutputBuilder.build("There is no 'name' arguments in command: ''{0}''", command.getCommand()).get();
        }

        String jarName = command.getArgument("name");

        PluginJarReport report = PluginJarReport.get();

        // unload first (because the physical removal deletes it from the jarContext)
        if (command.hasArgument("unload")) {
            if (this.pluginJarLoader.unload(jarName)) {
                report.incrementCounter(PluginJarReport.Counter.UNLOAD);
            }
        }

        // then delete it physically
        report.merge(this.jarUpdaterService.remove(jarName));

        return CommandOutputUtils.build(report);
    }
}
