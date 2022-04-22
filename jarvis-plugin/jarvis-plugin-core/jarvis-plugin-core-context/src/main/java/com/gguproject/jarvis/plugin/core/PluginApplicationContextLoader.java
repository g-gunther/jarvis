package com.gguproject.jarvis.plugin.core;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBus;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.ioc.ApplicationContextManager;
import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.spi.PluginSystemService;
import com.gguproject.jarvis.plugin.spi.data.PluginData;

/**
 * Implementation of {@link PluginSystemService}
 * It loads the {@link ApplicationContext} if not done yet
 * by scanning the jar
 */
public class PluginApplicationContextLoader implements PluginSystemService {

    @Override
    public void initialize(PluginData data, EventBus eventBus) {
        // initialize the logger
        AbstractLoggerFactory.init(data.getName());
        ClassLoaderContext.init(data.getName(), data.getVersion());

        Logger logger = AbstractLoggerFactory.getLogger(PluginApplicationContextLoader.class);
        logger.debug("Initialize plugin: {} - {}", data.getName(), data.getVersion());

        ApplicationContext context;
        if (!PluginApplicationContextAware.isApplicationContextLoaded()) {
            logger.debug("Initialize plugin context");

            context = ApplicationContextManager.get()
                    .scan("com.gguproject.jarvis")
                    .getContext();
        } else {
            context = PluginApplicationContextAware.getApplicationContext();
        }

        EventBusService eventBusService = context.getBean(EventBusService.class);

        // set the event bus service name to flag event listeners
        eventBusService.setName(data.getName());

        // may be null if the plugin has to initialize the event bus itself
        if (eventBus != null) {
            eventBusService.setEventBus(eventBus);
            eventBusService.registerListeners(context.getBeans(AbstractEventListener.class));
        } else {
            logger.info("Event bus is null");
            // do not register listener here because it requires the eventBus
        }

        // retrieve all launcher and process them
        context.getBeans(PluginLauncher.class).forEach(launcher -> launcher.launch());
    }
}