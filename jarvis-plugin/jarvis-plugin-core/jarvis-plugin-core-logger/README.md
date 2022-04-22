# jarvis-plugin-core-logger

This module provides some classes to write logs. This can only be used at plugin level and should not be provided by the core system
because of the logger configuration which includes the plugin name. This configuration is defined at the classloader level.

To get the logger: 

```java
class MyClass {
    private static final Logger Logger = AbstractLoggerFactory.getLogger(MyClass.class);
}
```

Then all the debug, info, warn and error methods are available.

It writes all the logs in the console and in the *logs/* folder. 
Each plugin has its own log file and all the logs are also written in a global.log file.
