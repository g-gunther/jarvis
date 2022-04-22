# jarvis-app-loader

Main project which bootstrap the application. This jar depends and has to depends only on the minimum required libraries.
Because every classes that are loaded by this application will then be shared by all the plugins since every plugin is loaded in its own classloader which is a child of the core system classloader. 

The main feature of this project is to download and load the `jarvis-plugin-loader` plugin.

The `plugins.properties` file should be created at the same location as the `jarvis-app-loader.jar` file.
This file contains the list of plugins to load. Example:

```
jarvis-plugin-tv
jarvis-plugin-command
```