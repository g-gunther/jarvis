# Todo

[] load plugins from another repo? github?
[] revoir orga de jarvis avec plusieurs pom plutôt que tout avec la même racine

# Introduction

This home automation project enables developping small plugins to interact with connected devices or performing actions of any kind (infrared commands, displaying data on connected web application - see magic-mirror project).

# Architecture

The conception of this project has been driven by few guidelines: 

- Try to not import a "big main Java library" suck as Spring for dependency injection. This objective is to be as light as possible.
- To be able to easily configure the application to interact with different services / devices (adding or removing a plugin) without having to rebuild it entirely
- The application should run on a Raspberry Pi (not every Java versions are available).
- Since some plugins may require a direct connection between the device to control and the Raspberry Pi (infrared, screen...), the application may be started several time on several Raspberry Pi and each of them should communicate to each other (1 instance in the living room may send an action to the instance in the bedroom for example).
- An instance of the application should not know that there are other instances of the applications in the network to avoid maintaining a central registry.   

## Software architecture

### Introduction

In order to be able to easily configure an instance of the application by adding or removing plugins without rebuilding it, the project has been developed based on the **microkernel architecture**.
There are 2 types of components: the core system and the plugins. 

![Microkernel architecture]()

Plugins are independent components and are not aware of the other loaded plugins. It's up to the developer to be sure every needed plugins are specified in the configuration file of the core system.   

The core system is only responsible for downloading, refreshing and loading the plugins based on provided the configuration file. 

Each plugin is composed of 2 elements:

- a jar file which contains the Java code of the plugin and all the dependencies it requires. This jar file must follow some guidelines to be loaded by the core system. 
- a data folder which contains all configuration files used by the jar file. This data folder is outside of the jar file to be able to update the plugin configuration without having to rebuild the jar file and reload it. 

To guarantee plugins independence, they are all loaded in their **own classloader**. A new classloader is created for every plugin jar files with the core system classloader as parent. 
By doing this, we are able to dynamically load and unload jar files.    

![Jarvis classloader architecture]()

---
**IMPORTANT**

But it means that **every library loaded in the core system will be shared by all child classloader**.

This is very important because 

- if a shared library is updated, we need to rebuild the core system and all the plugins using it, and re-deploy all of them
- since classes will be shared by all plugins, it may have some side effect
---

The core system by itself does not load all the plugins (to avoid having a lot of shared libraries). Instead, it will download and load the *main* plugin loader: jarvis-plugin-loader. 
This plugin is really responsible for parsing the configuration file, downloading and loading all the plugin jar files (and their data folder). 

### Implementation

#### Communication

##### External

Several instances of the application with various plugins may run across the house and all connected to the same network.
To avoid maintaining a registry of all the running instances, the list of their plugins... they communicate by broadcasting events using multicast.
It basically means when an instance needs to send an event to other instances, this event is serialized, then send over the network on ip address `224.0.0.1` on port `9999`
and every other application listening on that port (if any) will receive and, deserialize it and process it. 

**Note**: when emitting an event externally, it will also be send internally to be processed by all plugins.

A received event implements the interface `com.gguproject.jarvis.core.bus.support.DistributedEvent` which is an envelope containing the following information:

- timestamp: send date of this event
- uuid: unique sender identifier - used to check if the received event has been sent by the same application to avoid processing it internally twice. 
- eventSender: ip address and port that can be used to reply directly to the sender.
- data: serialized content of the event.

Since a DistributedEvent contains the sender connection information, it is possible to answer directly without having to broadcast to all other instances. 
It can be done using the `eventSender` property. By default, every instance listen to TCP messages on port `9997` and then propagate the event as multicast events.

Each received DistributedEvent are then propagated to all plugins of the instance using the `EventBus` described below.

##### Internal

Since every plugin is independent of the others but still need to interact with the other plugins or systems, an event bus is created (in the plugin `jarvis-plugin-loader` which loads all the other plugins) and shared by all the plugins. 
This event bus allows us to register or unregister event listeners and emit events.

An event has to extend the class `com.gguprojec.jarvis.core.bus.support.EventData` and is identified by its type and a unique name. 
It can then have attributes that characterized it.

```java
public class ExampleEventData extends EventData {
    private static final long serialVersionUID = -6841671964817624557L;
    public static final String eventType = "EXAMPLE_EVENT";

    /** Value to transmit */
    private String value;

    /** Empty constructor needed to deserialize the event */
    public ExampleEventData() {
        super(eventType, ExampleEventData.class);
    }

    public ExampleEventData(String value) {
        this();
        this.value = value;
    }
}
```

Then a plugin which has to listen to some particular events has to create listener beans by implementing the interface `com.gguproject.jarvis.core.bus.EventListener`.
There is an abstract class implementing it to make things easier `com.gguproject.jarvis.core.bus.AbstractEventListener`.
Those event listeners need to specify which type of event they are looking at.

```java
@Named
public class ExampleEventListener extends AbstractEventListener<ExampleEventData> {

    @Inject
    private SomeService someService;

    public ExampleEventListener() {
        super(ExampleEventData.eventType, ExampleEventData.class);
    }

    @Override
    public void onEvent(DistributedEvent event, ExampleEventData data) {
        this.someService.process(data.getValue());
    }
}
```

Event listeners are automatically registered in the event bus (because they implement the EventListener interface).
When an event has to be processed, the EventBus looks for all registered listeners for this particular event type and call each of them.

##### Event emission

To emit an event in this internal bus, the `EventBusService` can be used. It provides useful methods:

- emit(DistributedEvent event): to emit a raw event - this shouldn't be used, prefer the `emit(EventData data)` method instead. This is the method called when receiving an external event. It deserializes the raw data to an `EventData` object.
- emit(EventData data): to emit an event internally to plugin listeners of this application instance.
- externalEmit(EventData data): to emit an event using multicast to all other application instances. It also emits the event internally.
- externalEmit(EventData, EventSender target): to emit an event to a specific target identified by an IP address and port.
- emitAndWait(DistributedEvent event): to emit a raw event and wait for the response of listeners - this shouldn't be used, prefer the `emitAndWait(EventData data)` method instead. 
- emitAndWait(EventData data): to emit an event internally to plugin listeners of this application instance and wait for their responses.  

#### Plugin initialization

As said, a plugin is a jar file which is loaded in its own classloader. Therefore, to initialize it and complete its registration to the core system, the plugin must implement an SPI (Service Provider Interface): `com.gguproject.jarvis.plugin.spi.PluginSystemService`.
During the plugin load, the system looks for implementation of this interface using the `ServiceLoader`. This mechanism requires to specify the implementations of this interface inside a configuration file in `resources/META-INF/services/com.gguproject.jarvis.plugin.spi.PluginSystemService` (see in jarvis-plugin-core-context maven module for an example).

There is a default `PluginSystemService` implementation: `com.gguproject.jarvis.plugin.core.PluginApplicationContextLoader` which is in charge of initializing the plugin by:

- Contextualizing the logger with the plugin information
- Scanning classes having @Named annotation and resolving their dependencies (identified by the @Inject annotation)
- Setting the event bus
- Searching for beans implementation the `com.gguproject.jarvis.plugin.core.PluginLauncher` interface and execute them
  - This can be used to start some processing at the startup of the application (like threads)

#### Configuration

Most of the plugins require some configuration files. It might be interesting to not embedded them in the compiled jar file in order to be able to update them without having to compile again the plugin, and deploy it.
A `data` folder can be created at the root of the project. This data folder has to be assemble and published using a maven plugin. See section build and run for more details.

There are 2 beans that can be used to retrieve some configuration properties: 

- **ApplicationConfiguration**: used to retrieve global configuration properties (check `jarvis-app-loader/src/resources/jarvis/configuration.properties`) or information like the current ip address
- **AbstractPluginConfiguration**: this class needs to be overridden in plugin if it needs to access some specific plugin configuration
  - The configuration file of the plugin has to be under the data folder and named `configuration.properties`
  - This class can also be used to retrieve other configuration files that might be in the `data` folder

Here is an example of usage: 

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

### Maven structure

Here is a description of the maven structure of the project (which may evolve):

- jarvis-app
  - jarvis-app-loader: The startup jar. It contains the *ApplicationStartup* class which start the jarvis application.
  - jarvis-app-master: This application is mainly used to send some commands to the remote jarvis application deployed somewhere on the network (for debugging, testing or remote control).
- jarvis-assembly: assembly file to package the data folder of each plugin as a zip file 
- jarvis-core: contains libraries loaded by the *jarvis-app-loader* project. These libraries are shared by all plugins, be careful when updating it.
  - jarvis-core-dependencies: useful classes such as utilities, abstract classes...
  - jarvis-core-repository: some services used to download and refresh jar files and their associated data zip file from the remote repository (used by *jarvis-app-loader* and *jarvis-plugin-loader*)
- jarvis-data-maven-plugin: maven plugin which load to the remote repository the jar file and its data zip file for a given plugin
- jarvis-plugin
  - jarvis-plugin-core: all the useful libraries which are used by plugins (listed below)
  - jarvis-plugin-feature: contains all the developed plugins. Plugins follow this structure: 
    - jarvis-plugin-[plugin name]-api: (optional) contains events and other classes that have to be shared with other plugins (like *SpeechEventData* which contains a detected speech that has to be processed by plugins).
    - jarvis-plugin-[plugin name]-core: contains the plugin logic (interaction with a remote service, a connected device, or wait for an external event like speech to occurs)

**Note**: since this is a personal project, every plugins are located inside the same maven parent module. In an ideal world, they should have their own maven project and repository to be fully independent.

List of jarvis-plugin-core libraries:

- jarvis-plugin-core-bus: all the classes used to define the internal bus of events and their listeners.
- jarvis-plugin-core-channel: used to emit and receive data using multicast or simple tcp.
- jarvis-plugin-core-configuration: contains beans that allow to retrieve the global application configuration or the current plugin configuration.
- jarvis-plugin-core-context: contains the classes used to initialize the plugin (default `PluginSystemService` implementation and `PluginLauncher` interface). It also contains a useful class to start a single plugin in standalone mode for development. 
- jarvis-plugin-core-ioc: used to scan all the current classloader of the plugin, build all the beans and resolve their dependencies.
- jarvis-plugin-core-logger: default logger wrapper - needs to be at the plugin level and not core system to be able to set up the plugin name as a logger property.
- jarvis-plugin-core-spi: only contains the `PluginSystemService` interface used by the ServiceLoader at the core system and jarvis-plugin-loader level.
- jarvis-plugin-core-helper: there are several helper libraries used to control some Raspberry Pi IO device (leds), play a sound, execute a shell command... 

List of plugins:

- jarvis-plugin-androidtv: simulate key press action for android tv  
- jarvis-plugin-cec: send some command using cec-client
- jarvis-plugin-command: used to interact with the application using command lines
- jarvis-plugin-display: send specific events to a web application (magic mirror for example)
- jarvis-plugin-freeboxv6: send http request to control a freebox v6
- jarvis-plugin-infrared: execute infrared commands with ir-ctl (to simulate a remote) 
- jarvis-plugin-kodi: control a kodi application
- (todo) jarvis-plugin-light: objectif - control phillips hue lights   
- jarvis-plugin-loader: download and loads configured plugins
- (todo) jarvis-plugin-log: logs all the events broadcasted on the network 
- (todo) jarvis-plugin-mode: orchestrates other plugins by sending multiple events when activating a mode (lights and music for example) 
- jarvis-plugin-speaker: transform the text to speech and play it
- jarvis-plugin-speech: transform speech to text using google or deepspeech
- jarvis-plugin-sphinx-speech: transform speech to text using the standalone library Sphinx 
- jarvis-plugin-spotify: control spotify on a targeted device
- jarvis-plugin-time: play timer or alarm sounds 
- jarvis-plugin-tv: translate speech text to remote controls using the jarvis-plugin-infrared plugin
- (todo) jarvis-plugin-vaccum: control a roborock vacuum 
- jarvis-plugin-weather: retrieve weather from a given location and display it on web application

# Build and run

## Repository

Based on 2 principles defined when the project started: 

- Being able to add or remove plugins without having to rebuild the application
- Enable service discovery across the network

An other application has been developed to act like as a plugin repository. It registers itself on the network using mdns.
Other applications are then able to request for that service by its name on the network and retrieve its ip address and port for further http calls.

This repository exposes several endpoints to:

- upload a plugin jar file by specifying the plugin name and version
- upload a zipped plugin data folder for a given plugin and version
- download the latest version of a plugin jar file and data file (or a specified version) 

When the application starts, it will look for this repository and if found, download all the configured plugins before executing them.

## Build

In order to publish the plugins and their data files to the repository, some maven configuration has to be added. 

In the following example, the configuration and use of plugins has been split to reuse it across several plugins.
First, here is the global pom.xml configuration:

```xml
<profiles>
    <!--
        For convenience, a profile has been created to avoid publishing the plugins every time 
        the project is built 
    -->
    <profile>
        <id>publish</id>
        <build>
            <pluginManagement>
                <plugins>
                    <!--  
                        Plugin that uploads the jar & data files to the repository 
                    -->
                    <plugin>
                        <groupId>com.gguproject.jarvis</groupId>
                        <artifactId>jarvis-data-maven-plugin</artifactId>
                        <version>0.0.1-SNAPSHOT</version>
                        <executions>
                            <!-- The first execution is for uploading data file-->
                            <execution>
                                <id>execution-data</id>
                                <phase>install</phase>
                                <configuration>
                                    <!-- 
                                        The host is optional. If specified as the repository address, it will be used. 
                                        Else it will look for the repository service using mdns and the name defined in the repositoryDnsServiceName property
                                    -->
                                    <host>localhost:8080</host>
                                    <repositoryDnsServiceName>jarvis.com.gguproject.jarvis:repository_v0.0.1._http.local.</repositoryDnsServiceName>
                                    <!-- Which file has to be uploaded (here the data zip file) -->
                                    <file>${basedir}/target/data-${output.name}-${project.version}.zip</file>
                                    <!-- And what is its target name on the repository -->
                                    <targetFilename>data-${output.name}-${project.version}.zip</targetFilename>
                                    <!-- Which API has to be called to upload it  -->
                                    <path>/jar/${output.name}/version/${project.version}/data</path>
                                </configuration>
                                <goals>
                                    <goal>repository-upload</goal>
                                </goals>
                            </execution>
            
                            <!-- Same configuration for the plugin jar file -->
                            <execution>
                                <id>execution-jar</id>
                                <phase>install</phase>
                                <configuration>
                                    <host>localhost:8080</host>
                                    <repositoryDnsServiceName>
                                        jarvis.com.gguproject.jarvis:repository_v0.0.1._http.local.
                                    </repositoryDnsServiceName>
                                    <file>
                                        ${basedir}/target/${output.name}-${project.version}-jar-with-dependencies.jar
                                    </file>
                                    <targetFilename>${output.name}-${project.version}.jar</targetFilename>
                                    <path>/jar/${output.name}/version/${project.version}</path>
                                </configuration>
                                <goals>
                                    <goal>repository-upload</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </pluginManagement>
        </build>
    </profile>
</profiles>

<build>
    <pluginManagement>
        <plugin>
            <artifactId>maven-assembly-plugin</artifactId>
            <dependencies>
                <!-- Common assembly file (for data and secret-data). Used to zip the data/ forlder to easly upload it to the repository -->
                <dependency>
                    <groupId>com.gguproject.jarvis</groupId>
                    <artifactId>jarvis-assembly</artifactId>
                    <version>0.0.1-SNAPSHOT</version>
                </dependency>
                <!-- Contains a containerDescriptorHandler used by the secret-data assembly to rename the root of the generated zip file with the plugin name-->
                <dependency>
                    <groupId>com.gguproject.jarvis</groupId>
                    <artifactId>jarvis-build-secret-data-zip-descriptor-handler</artifactId>
                    <version>0.0.1-SNAPSHOT</version>
                </dependency>
            </dependencies>
        
            <executions>
                <!-- 
                    Package application project to a jar with all its dependencies
                    and a specified main class 
                -->
                <execution>
                    <id>make-app-assembly</id>
                    <phase>none</phase>
                    <configuration>
                        <descriptorRefs>
                            <descriptorRef>jar-with-dependencies</descriptorRef>
                        </descriptorRefs>
                        <archive>
                            <manifest>
                                <mainClass>com.gguproject.jarvis.ApplicationStartup</mainClass>
                            </manifest>
                        </archive>
                        <finalName>${output.name}-${project.version}-jar-with-dependencies</finalName>
                        <appendAssemblyId>false</appendAssemblyId>
                    </configuration>
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>
        
                <!-- 
                    Package a plugin jar with all its dependencies 
                -->
                <execution>
                    <id>make-plugin-assembly</id>
                    <phase>none</phase>
                    <configuration>
                        <descriptorRefs>
                            <descriptorRef>jar-with-dependencies</descriptorRef>
                        </descriptorRefs>
                        <finalName>${output.name}-${project.version}-jar-with-dependencies</finalName>
                        <appendAssemblyId>false</appendAssemblyId>
                    </configuration>
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>
        
                <!-- 
                    Package data to a zip file (uses the descriptor file in projet: jarvis-assembly)
                -->
                <execution>
                    <id>make-data-assembly</id>
                    <phase>none</phase>
                    <configuration>
                        <descriptorRefs>
                            <descriptorRef>data-assembly</descriptorRef>
                        </descriptorRefs>
                        <finalName>data-${output.name}-${project.version}</finalName>
                        <appendAssemblyId>false</appendAssemblyId>
                    </configuration>
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>

                <!-- 
                    Package secret-data to a zip file (uses the descriptor file in projet: jarvis-assembly)
                -->
                <execution>
                    <id>make-secret-data-assembly</id>
                    <phase>none</phase>
                    <goals>
                        <goal>single</goal>
                    </goals>
                    <configuration>
                        <descriptorRefs>
                            <descriptorRef>secret-data-assembly</descriptorRef>
                        </descriptorRefs>
                        <finalName>secret-data-${output.name}-${project.version}</finalName>
                        <appendAssemblyId>false</appendAssemblyId>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </pluginManagement>
</build>
```

Then here is an example of a classic plugin project: jarvis-plugin-mypluginname-core for example: 

```xml
<profiles>
    
    <!-- 
        Configuration the "publish" profile defined on the main pom.xml
        which will zip the data folder and execute the publishing plugin
        to send them to the repository    
    -->
    <profile>
        <id>publish</id>
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-assembly-plugin</artifactId>
                    <executions>
                        <!-- To add only if there is a data/ folder to publish-->
                        <execution>
                            <id>make-data-assembly</id>
                            <phase>package</phase>
                        </execution>
                        <!-- To add if there is a secret-data/ folder to zip for manual deployment -->
                        <execution>
                            <id>make-data-assembly</id>
                            <phase>package</phase>
                        </execution>
                    </executions>
                </plugin>
                
                <plugin>
                    <groupId>com.gguproject.jarvis</groupId>
                    <artifactId>jarvis-data-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>

<!--
    By default build the plugin jar file as it should be. It can be used for development purposes 
-->
<build>
    <plugins>
        <plugin>
            <artifactId>maven-assembly-plugin</artifactId>
            <executions>
                <execution>
                    <id>make-plugin-assembly</id>
                    <phase>package</phase>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Then to build and publish those files, the command `mvn clean install -Ppublish` has to be executed.

## Deploy

Once every plugin has been published, the application can now be installed on the Raspberry and launch.

First, the main application jar has to be installed. It can be done by using the scp command to copy the jar file with all its dependencies (located in jarvis-app/jarvis-app-loader/target after a build): 

```
scp jarvis-app-loader-[version]-jar-with-dependencies.jar [username]@[ip address]:[installation folder]/jarvis-app-loader-0.0.1-SNAPSHOT.jar
```

Then, in the [installation folder], create a `plugins.properties` file which will contain the list of plugin to download and load. Example:

```
jarvis-plugin-weather
jarvis-plugin-command
```

**Note**: for now it's not possible to specify a specific version for a given plugin. It will always download the latest version.

If one of the plugins uses the helper library `jarvis-plugin-helper-led` it requires an additionnal file in the [installation folder]: `libpi4j-pigpio.so`.
At the time this documentation is being written, a bug in the version 2.0 of pi4j (which is used to interact with the Raspberry I/O) requires a modification on the `libpi4j-pigpio.so` file.
This file can be found at the root of the jarvis project.

If one of the plugins has some secret-data files, a `secret-data` folder has to be created in the [installation folder] and you can either move the secret-data zip file which has been generated using the 
assembly `secret-data-assembly` and unzip it by using the command:

```
scp secret-data-[plugin name]-[plugin version].zip [username]@[ip address]:[installation folder]/secret-data/[plugin-name].zip; ssh [username]@[ip address] 'unzip [installation folder]/secret-data/[plugin-name].zip'
```

or move/create the file directly in a sub folder of `secret-data` having the same name as the plugin which needs it (`secret-data/my-plugin-name` for example).

## Run

Once the `jarvis-app-loader` jar and the `plugins.properties` file are on the Raspberry, the application can be started by executing it: 

```
java -jar jarvis-app-loader-0.0.1-SNAPSHOT.jar
```

If the file `libpi4j-pigpio.so` needs to be overriden, then run the following command instead: 

```
java -jar -Dpi4j.library.path="/home/pi/jarvis/" jarvis-app-loader-0.0.1-SNAPSHOT.jar
```

# Useful notes

## Raspberry installation

Some useful notes to install the Raspberry (to avoid looking for them every time the device has to be re-installed).

Some plugins requires additionnal libraries / package, please check on the README.md of those plugins for more details.

### Raspbian

1. Download the last version of raspbian: [here](https://www.raspberrypi.org/downloads/raspbian/)
2. Follow the [guide](https://www.raspberrypi.org/documentation/installation/installing-images/mac.md)
3. To enable the wifi, follow the steps described in the next section
4. First authentication with default username/password: pi/raspberry
5. To the password run: `sudo raspi-config`

To update the system:

1. `sudo apt-get update`
2. `sudo apt-get upgrade`
3. `sudo apt-get install raspberrypi-bootloader`

### Wifi

Once raspbian has been installed on the SD card, follow the [guide](https://www.raspberrypi.org/documentation/configuration/wireless/headless.md).

Also create an empty `ssh` file at the root of the SD card to activate the ssh module by default.

The Raspberry will be accessible by ssh using its ip address. To force using a static ip address, follow this [guide](https://pimylifeup.com/raspberry-pi-static-ip-address/)

### Java

Java can be installed using the command: `sudo apt-get update && sudo apt-get install openjdk-11-jdk`

### USB Speaker

To enable a USB speaker, follow this [guide](https://raspberrypi.stackexchange.com/questions/80072/how-can-i-use-an-external-usb-sound-card-and-set-it-as-default)

- Run command `cat /proc/asound/modules` 
- retrieve the card number associated to the USB device
- Do the same with `cat /proc/asound/cards`
- Remove the jack speaker in `sudo nano /boot/config.txt` file and put `dtparam=audio=off`
- open `sudo nano ~/.asoundrc` and specify the card number: 
```
pcm.!default {
        type hw
        card 1
}

ctl.!default {
        type hw
        card 1
}
```

- Do the same in `sudo nano /usr/share/alsa/alsa.conf` file by setting: 
```
defaults.ctl.card 1
defaults.pcm.card 1
```
By executing `alsamixer`, the volume gauge should at the maximum. It has to be decreased a little bit else it might not work (bug).
A test can be done by downloading a sound file: `wget https://www.kozco.com/tech/piano2.wav` and playing it: `aplay piano2.wav` or `speaker-test -c2`

### Screen

Here are some useful links to enable a small screen Raspberry Pi 4 7" Touchscreen:

- how to install it: [video](https://www.youtube.com/watch?v=J69-bxOSMC8&ab_channel=ETAPRIME)
- how to execute Google Chrome automatically at startup: [guide](https://blog.r0b.io/post/minimal-rpi-kiosk/) and for [information](https://die-antwort.eu/techblog/2017-12-setup-raspberry-pi-for-kiosk-mode/)

## Known issues

### Can't load log handler "java.util.logging.FileHandler

At first startup, the following error:  `Can't load log handler "java.util.logging.FileHandler"` might happen. 
The application should have access to the `logs` folder: `sudo chmod -R 777 logs` (for example).

### DNS server not found

The application might not be able to retrieve the repository server ip address even if it's well registered on the network.

A possible issue might come from the router or internet box if this one is configured to assign IPv6 addresses.
Try by deactivating this option. 

### Development - Configuration file not found

During development, when starting a plugin in standalone mode, the configuration file `data/configuration.properties` might not been found.
Check that execution configuration to ensure the working directory is the directory of the plugin project and not the root of the jarvis project (defaut value).

### IPv4 vs Ipv6

During startup, an error may occurs because the JVM prefers IPv6 over IPv4. Try using this option: `-Djava.net.preferIPv4Stack=true` 