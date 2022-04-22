package com.gguproject.jarvis.plugin.core;

public class ClassLoaderContext {

    private static String name;

    private static String version;

    public static void init(String name, String version) {
        ClassLoaderContext.name = name;
        ClassLoaderContext.version = version;
    }

    public static String getName() {
        return ClassLoaderContext.name;
    }

    public static String getVersion() {
        return ClassLoaderContext.version;
    }
}
