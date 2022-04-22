package com.gguproject.jarvis.plugin.loader.jarloader.updater;

public class PluginJarContextDto {

    private String name;

    private String version;

    private boolean loaded;

    public PluginJarContextDto(String name, String version, boolean loaded) {
        this.name = name;
        this.version = version;
        this.loaded = loaded;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
