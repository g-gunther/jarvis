package com.gguproject.jarvis.plugin.loader.jarloader;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.core.ioc.utils.ClassLoaderException;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarContext;
import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarException;
import com.gguproject.jarvis.plugin.spi.PluginSystemService;

import javax.inject.Named;
import java.util.*;

/**
 * Utility class used to load a list of jars on their own class loader
 * to be extended in order to specify the service class
 * (used to check if there is already a jar loaded in the current classLoader - dev mode vs production)
 */
@Named
public class PluginJarLoader {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(PluginJarLoader.class);

    /**
     * List of initializers to execute on each loaded jar
     */
    private Map<Integer, List<PluginJarLoaderInitializer>> initializer = new HashMap<>();

    /**
     * Class which is used to initialize the jar
     */
    private Class<?> service;

    /**
     * Indicates if plugins are embedded in the current classloader
     */
    private boolean isPluginInCurrentClassLoader;

    private final EventBusService eventBusService;

    private final PluginJarService pluginJarService;

    public PluginJarLoader(EventBusService eventBusService, PluginJarService pluginJarService) {
        this.eventBusService = eventBusService;
        this.pluginJarService = pluginJarService;

        this.service = PluginSystemService.class;

        // if the service class can be found, it means there are plugins embedded in the app
        this.isPluginInCurrentClassLoader = ServiceLoader.load(this.service).iterator().hasNext();
    }

    /**
     * Add an initializer to execute on each loaded jar
     *
     * @param order       Indicates the rank of the initializer. The slowest will be executed first
     * @param initializer Initializer to execute
     * @return Instance of jar loader
     */
    public PluginJarLoader addInitialize(int order, PluginJarLoaderInitializer initializer) {
        if (!this.initializer.containsKey(order)) {
            this.initializer.put(order, new ArrayList<>());
        }
        this.initializer.get(order).add(initializer);
        return this;
    }

    /**
     * Process the jar loader
     */
    public void process() {
        // there are jars inside the main class loader (check the app pom.xml) and some others to be loaded externally.
        // can't do both -> leave
        if (this.isPluginInCurrentClassLoader && !this.pluginJarService.getContexts().isEmpty()) {
            throw new TechnicalException("Can't load plugins that are inside the main class loader and external plugins");
        }

        if (this.isPluginInCurrentClassLoader) {
            // initialize the jars with an empty class loader which means jars has been loaded in the
            // parent class loader (for dev)
            this.initializeJar(PluginJarContext.EMPTY);
        } else {
            // then load jars
            this.loadJarContexts();
        }
    }

    /**
     * Add and load a new jar context
     *
     * @param pluginJarContext context to load
     */
    public boolean load(PluginJarContext pluginJarContext) {
        LOGGER.debug("Try to add and load jar: {}", pluginJarContext.getName());

        if (this.isPluginInCurrentClassLoader) {
            LOGGER.warn("Can't load jar context '{}' because some plugins has been found in the current classloader", pluginJarContext);
            return false;
        }

        if (this.pluginJarService.getContexts().stream().filter(c -> c.getName().equals(pluginJarContext.getName()) && c.isLoaded()).findFirst().isPresent()) {
            LOGGER.warn("Can't load jar context '{}' because it is already loaded", pluginJarContext);
            return false;
        }

        this.loadJarContext(pluginJarContext);
        return true;
    }

    /**
     * Load all plugins that are in plugin directory
     * Will initialize a class loader for each found jars
     */
    private void loadJarContexts() {
        this.pluginJarService.getContexts().forEach(c -> this.loadJarContext(c));
    }

    /**
     * Load a given jar context
     *
     * @param pluginJarContext
     */
    private void loadJarContext(PluginJarContext pluginJarContext) {
        if (!pluginJarContext.isLoaded()) {
            LOGGER.info("Load jar: {}", pluginJarContext.getName());
            try {
                pluginJarContext.load((context) -> {
                    this.initializeJar(context);
                });
            } catch (PluginJarException e) {
                LOGGER.error("Can't load jar {}", pluginJarContext.getName(), e);
            }
        }
    }

    /**
     * Initialize a plugin by its class loader
     *
     * @param pluginJarContext
     * @throws ClassLoaderException
     */
    private void initializeJar(PluginJarContext pluginJarContext) {
        this.initializer.entrySet().stream()
                .sorted((c1, c2) -> c1.getKey() < c2.getKey() ? -1 : 1)
                .map(e -> e.getValue())
                .flatMap(List::stream)
                .forEach(init -> {
                    init.initialize(pluginJarContext);
                });
    }


    /**
     * Unload a given jar context by its name
     *
     * @param jarName
     */
    public boolean unload(String jarName) {
        LOGGER.debug("try to unload jar: {}", jarName);
        Optional<PluginJarContext> o = this.pluginJarService.getContexts().stream().filter(c -> c.getName().equals(jarName)).findFirst();
        if (o.isEmpty()) {
            LOGGER.warn("No jar context with name '{}' to unload found", jarName);
            return false;
        }

        this.eventBusService.unregisterListenersByName(jarName);

        try {
            o.get().unload();
        } catch (PluginJarException e) {
            LOGGER.error("Can't unload jar {}", o.get().getName(), e);
            return false;
        }

        return true;
    }
}
