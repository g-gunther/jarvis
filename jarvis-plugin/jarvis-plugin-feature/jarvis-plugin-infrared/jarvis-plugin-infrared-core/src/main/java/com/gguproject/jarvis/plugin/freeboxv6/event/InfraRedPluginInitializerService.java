package com.gguproject.jarvis.plugin.freeboxv6.event;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.core.PluginLauncher;
import com.gguproject.jarvis.plugin.freeboxv6.event.analyser.CommandAnalyser;

import javax.inject.Named;

@Named
public class InfraRedPluginInitializerService implements PluginLauncher {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(InfraRedPluginInitializerService.class);

    private final CommandAnalyser commandAnalyser;

    public InfraRedPluginInitializerService(CommandAnalyser commandAnalyser){
        this.commandAnalyser = commandAnalyser;
    }

    public void launch() {
        LOGGER.debug("Initialize the infrared plugin");
        commandAnalyser.loadConfiguration();
    }
}
