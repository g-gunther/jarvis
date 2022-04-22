# jarvis-plugin-core-configuration

Most of the plugins require some configuration files. It might be interesting to not embedded them in the compiled jar file in order to be able to update them without having to compile again the plugin, and deploy it.
A `data` folder can be created at the root of the project. This data folder has to be assemble and published using a maven plugin. 
Another folder `secret-data` can also be created at the root of the project to contains "secret" information like credentials. This folder is added to the .gitignore and won't be published in the plugin repository. 
It means that it has to be copied manually on the device that will run the application. 
See section build and run for more details.

There are 2 beans that can be used to retrieve some configuration properties: 

- **ApplicationConfiguration**: used to retrieve global configuration properties (check `jarvis-app-loader/src/resources/jarvis/configuration.properties`) or information like the current ip address
- **AbstractPluginConfiguration**: this class needs to be overridden in plugin if it needs to access some specific plugin configuration
  - loads all property files under the `secret-data` folder
  - loads all property files under the `data` folder
  - This class can also be used to retrieve other configuration files that might be in the `data` or `secret-data` folder

# Usage

## Simple usage

```java
@Named
public class MyPluginConfiguration extends AbstractPluginConfiguration {

    public MyPluginConfiguration() {
        // specify here the plugin name - the same one which is used to publish it 
        super("jarvis-plugin-myplugin");
    }

    public class PropertyKey {
        public static final String someProperty = "my.property.key";
    }
}
```

And the configuration file in jarvis-plugin-myplugin/data/configuration.properties 

```
my.property.key=My Property Value
```

```java
@Named
public class MyService {
    
    @Inject
    private MyPluginConfiguration configuration;

    public void test(){
        String value = this.configuration.getProperty(PropertyKey.someProperty);
    
        // to get another configuration file
        this.getConfigurationFile("other_configuration_file.properties");
    }
}
```

## Placeholder and secret properties

Since "secret" files are not shared, it's easier to use placeholders on classic property files that will be replaced by the secret properties:

In file: data/configuration.properties
```
my.property.key=${secret.my.property.key}
```

In file: secret-data/my-credentials.properties
```
secret.my.property.key=my secret value
```

The `secret-data` files are loaded before `data`files so the placeholders ${...} can be replaced. Then the property `my.property.key` can be used as seen previously.

### Deployment

Since the `secret-data` folder is not part of the plugin publishing, it won't be loaded on the raspberry pi or other device where the application runs.
It needs to be added manually at the root of the application following the structure:

- jarvis-app-loader.jar
- plugins/
- secret-data/
  - my-plugin-name/
    - files that are in the `secret-data` folder
  - another-plugin-name/
    - other secret files

Those files won't be reloaded, removed or added by the jarvis application like the normal `data` folder.

To simplify this operation, when building a plugin with a `secret-data` folder, a zip file `secret-data-[plugin name]-[plugin version].zip` is created.
It can be moved to the remote device and unzip using the command: 

```
scp secret-data-[plugin name]-[plugin version].zip [username]@[ip address]:[installation folder]/secret-data/[plugin-name].zip; ssh [username]@[ip address] 'unzip [installation folder]/secret-data/[plugin-name].zip'
```