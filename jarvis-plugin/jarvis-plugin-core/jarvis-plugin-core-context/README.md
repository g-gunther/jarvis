# jarvis-plugin-core-context

This module is used to boostrap the plugin. It contains all the required dependencies (such as ioc, logger, spi...)
but also provides the implementation of `PluginSystemService` which is used to load the plugin by setting the event bus.

This implementation:

- scans all packages starting by `com.gguproject.jarvis` to set up the context by creating the beans and resolves their dependencies.
- set the event bus and initialize it (look for listeners and register them)
- look for beans implementing `PluginLauncher` and call the *launch* method. This is used to start some specific features on the plugin

This module also provides an abstract class to start a given plugin in standalone for development purpose: `AbstractPluginDevStartup`.
It loads and starts the plugin as if it was done by the plugin loader. 
 