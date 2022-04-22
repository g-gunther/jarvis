package com.gguproject.jarvis.plugin.loader.jarloader;

import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarContext;

@FunctionalInterface
public interface PluginJarLoaderInitializer {

    /**
     * Callback to execute when a plugin is loaded
     *
     * @param classLoader the jar class loader
     */
    public void initialize(PluginJarContext classLoader);
}
