# jarvis-core-repository

This project provides services to access the remote repository. Here are the features:

- list all the available plugins and their last version
- retrieve the information for a given plugin (name, full name and version)
- download the last version of a specified plugin
- download the data zip file of a specified plugin and version

It is used by the core system to download the `jarvis-plugin-loader` and by this plugin to download all the other plugins.