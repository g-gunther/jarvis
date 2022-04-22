package com.gguproject.jarvis.plugin.loader;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBus;
import com.gguproject.jarvis.core.bus.EventBusImpl;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.core.ClassLoaderContext;
import com.gguproject.jarvis.plugin.core.PluginLauncher;
import com.gguproject.jarvis.plugin.loader.jarloader.PluginJarLoader;
import com.gguproject.jarvis.plugin.loader.jarloader.PluginJarService;
import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarContext;
import com.gguproject.jarvis.plugin.spi.PluginSystemService;
import com.gguproject.jarvis.plugin.spi.data.PluginData;

import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

@Named
public class LoaderPluginLauncher implements PluginLauncher {


    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(LoaderPluginLauncher.class);

    private final PluginJarService pluginService;

    private final PluginJarLoader pluginLoader;

    private final EventBusService eventBusService;

    private final List<AbstractEventListener> eventListeners;

    /**
     * Event bus to communicate with all loaded plugins
     */
    private EventBus eventBus = new EventBusImpl();

    public LoaderPluginLauncher(PluginJarService pluginService, PluginJarLoader pluginLoader, EventBusService eventBusService, List<AbstractEventListener> eventListeners){
        this.pluginService = pluginService;
        this.pluginLoader = pluginLoader;
        this.eventBusService = eventBusService;
        this.eventListeners = eventListeners;
    }

    public void launch() {
        LOGGER.info("Launch the app plugin");

        // set the eventBus instance & register listeners of this particular plugin since it hasn't been done
        // in the PluginApplicationContextLoader
        this.eventBusService.setEventBus(eventBus);
        eventBusService.registerListeners(this.eventListeners);

        // update all plugin jars & exclude this current plugin
        this.pluginService.process(ClassLoaderContext.getName());

        // Set the classloader on the current plugin (which has been loaded by the jarvis-app-loader)
        Optional<PluginJarContext> currentModuleContext = pluginService.getContexts().stream().filter(context -> context.getName().equals(ClassLoaderContext.getName())).findFirst();
        if (currentModuleContext.isPresent()) {
            currentModuleContext.get().setClassLoader(this.getClass().getClassLoader());
        }

        // load all plugins with the given event bus
        // load all system modules and initialize them
        if (!this.pluginService.getContexts().isEmpty()) {
            this.pluginLoader
                    .addInitialize(1, (jarContext) -> {
                        // call it only if the class loader is not null
                        // it means the plugin has been loaded with its own class loader and all the
                        // system stuff has to be initialized
                        if (jarContext.getClassLoader() != null) {
                            ServiceLoader.load(PluginSystemService.class, jarContext.getClassLoader()).forEach(s -> {
                                s.initialize(new PluginData(jarContext.getName(), jarContext.getVersion()), eventBus);
                            });
                        }
                    })
                    .process();
        }
    }
}
