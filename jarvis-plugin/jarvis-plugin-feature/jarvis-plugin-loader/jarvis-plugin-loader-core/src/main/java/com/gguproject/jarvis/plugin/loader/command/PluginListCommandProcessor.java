package com.gguproject.jarvis.plugin.loader.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.loader.jarloader.PluginJarService;
import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarContextDto;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named
@Qualifier(PluginCommandProcessor.qualifier)
public class PluginListCommandProcessor extends AbstractCommandProcessor {

    private final PluginJarService jarUpdaterService;

    public PluginListCommandProcessor(PluginJarService jarUpdaterService) {
        super("list", "list all plugin");
        this.registerArgumentDescription("<no arg>", "List loaded jars");
        this.registerArgumentDescription("repo", "List all jars that are in the repository");
        this.jarUpdaterService = jarUpdaterService;
    }

    @Override
    public CommandOutput process(CommandRequest command) {
        // list all jars contained in the repository
        if (command.hasArgument("repo")) {
            Map<String, String> jars = this.jarUpdaterService.listRepositoryJars();
            if (jars.isEmpty()) {
                return CommandOutputBuilder.build("Nothing in repository").get();
            }
            CommandOutputBuilder builder = CommandOutputBuilder.build();
            jars.entrySet().forEach(e -> {
                builder.append("{0} : {1}", e.getKey(), e.getValue()).newLine();
            });
            return builder.get();
        }

        // else list all loaded jars
        List<PluginJarContextDto> loadedPluginNames = this.jarUpdaterService.listAvailableJars();
        if (loadedPluginNames.isEmpty()) {
            return CommandOutputBuilder.build("Nothing has been loaded").get();
        } else {
            CommandOutputBuilder builder = CommandOutputBuilder.build();
            loadedPluginNames.forEach(context -> {
                builder.append("{0} : {1}{2}", context.getName(), context.getVersion(), context.isLoaded() ? " (loaded)" : "").newLine();
            });
            return builder.get();
        }
    }
}