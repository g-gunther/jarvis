# jarvis-plugin-loader

This is the only 

## Events

### Input

list of listened events: 

**PLUGIN_CONFIGURATION** (PluginConfigurationEventData): to add or remove a plugin from the current loader  
events attributes:  
- pluginName: name of the plugin to add/remove
- action: ADD or REMOVE

This event is not yet implemented.

## Commands

Implemented commands:

1. plugin add -name={name} -load  
{name}: name of the plugin to add  
{load}: (optional) if present, the plugin will be downloaded
2. plugin remove -name={name} -unload  
{name}: name of the plugin to remove  
{unload}: (optional) if present the plugin will be unloaded from the application
3. plugin list
4. plugin refresh -available -all -name={name}  
{available}: refresh the already loaded plugins  
{all}: refresh all the plugins  
{name}: refresh a specific plugin
5. plugin data-refresh -available -name={name}  
{available}: reload the data folder a all available plugins     
{name}: reload the data folder of a given plugin  

## Implementation

The loader process follows several steps:

1. First it lists all the folders in the `plugins` directory and parse them to get the plugin name and its version
2. Parse the `plugins.properties` file to get the list of plugins to load
3. For each found plugins in the `plugins` directory, if also present in the `plugins.properties` file then it is refreshed (it means that if there is a newer version in the repository, it will be downloaded), else removed.
4. For each plugins in the `plugins.properties` file but no in the `plugins` directory, download it.

For each refreshed/downloaded plugins, its jar file and data folder are downloaded from the repository.

When all plugins are downloaded, each plugin jar files will be loaded in its own classloader.
The using SPI, implementation of the `PluginSystemService` service are detected in the loaded jar and called in order to initialize the plugins by
passing them their name, version and the implementation of the `eventBus` which will be shared across all the plugin.

This loader plugin also implements a specific event listener: `ExternalEventData`. This event is defined in the bus modules and allows plugins to 
send data externally. 

## Configuration

Uses the `plugins.properties` file defines at the root of the project to determine the plugins to download/remove/refresh.