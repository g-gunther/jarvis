package com.gguproject.jarvis.plugin.command;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.core.PluginLauncher;

import javax.inject.Named;

@Named
public class CommandPluginInitializerService implements PluginLauncher {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(CommandPluginInitializerService.class);

	private final SystemCommandReceiver systemCommandReceiver;

	public CommandPluginInitializerService(SystemCommandReceiver systemCommandReceiver){
		this.systemCommandReceiver = systemCommandReceiver;
	}

	public void launch() {
		LOGGER.debug("Initialize the command plugin");
		systemCommandReceiver.start();
	}
}
