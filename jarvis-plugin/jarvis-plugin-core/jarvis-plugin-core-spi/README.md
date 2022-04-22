# jarvis-plugin-core-spi

A plugin is a jar file which is loaded in its own classloader. Therefore, to initialize it and complete its registration to the core system, the plugin must implement an SPI (Service Provider Interface): `com.gguproject.jarvis.plugin.spi.PluginSystemService`.
During the plugin load, the system looks for implementation of this interface using the `ServiceLoader`. This mechanism requires to specify the implementations of this interface inside a configuration file in `resources/META-INF/services/com.gguproject.jarvis.plugin.spi.PluginSystemService` (see in jarvis-plugin-core-context maven module for an example).

This module contains the interface which is one of the only shared interface/class between the core system and the plugins.