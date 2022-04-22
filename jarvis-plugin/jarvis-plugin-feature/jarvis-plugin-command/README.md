# jarvis-plugin-command

This plugin can be considered as a core plugin because it adds a transverse feature to all other loaded plugins.

A command in this context is like a shell command. It has the following format: `command1 command2 -argName=argValue -otherArg`. 
Each plugin can expose one or several commands which can be triggered. 

Usually commands are sent by the `jarvis-app-master` application to all the instances in order to interact with them.
Once processed, each instance send back an output with a status (success, error, not found) and some output text if needed. 

## API

This plugin exposes 2 events:

- **ExternalCommandRequestEventData** (EXTERNAL_COMMAND_REQUEST): used to send a command formatted like described above.
- **ExternalCommandResponseEventData** (EXTERNAL_COMMAND_RESPONSE): used by the plugin to send back the output of the processing.
  - It contains the original command
  - and the list of every plugins results. Each of these result contains a status (success, error or not found if the plugin can't process this query), and an output text.   

## Implementation

The plugin itself listens to **ExternalCommandRequestEventData** events. 
On reception, it emits internally the command using `CommandProcessorEventData`. 
Every other plugins that expose commands, listen to these specific event to process it.

It then wait that all plugins process the command, aggregate all the results and send it back to the sender only as a response of the received query event. 

On startup it also starts a console input listener. 
This is very useful for debugging when starting the application (not in a headless mode). 
It allows us to directly enter some commands and on *enter* it processes it internally only (it won't be dispatch to the other jarvis instances).

### Exposing commands

Every plugin that needs to expose some commands needs to depend on the `jarvis-plugin-command-processor` module.

First of all, this module provides the `CommandProcessorEventData` listener which is required to process the received events.
This listener uses the `CommandManager` to interact with all command processors.

To expose a command processor:

- First create a class that extends `AbstractParentCommandProcessor`. This class defines the "domain" of the action (it's related to the plugin name: music, tv, vaccum, light...)
  - When defining the bean with the @Named annotation, also add this annotation: `@Qualifier(AbstractCommandProcessor.rootQualifier)`. It indicates that it is level one command.
  - It also needs a qualifier name which will be used by child command processors to link them to the right parent
  - The constructor takes in argument the domain name which will be used in the command query, and a description for help
  - It injects itself the list of `AbstractCommandProcessor` that have its qualifier name
Here is an example:
   
```java
@Named
@Qualifier(AbstractCommandProcessor.rootQualifier)
public class TvCommandProcessor extends AbstractParentCommandProcessor{
	public static final String qualifier = "tvCommandProcessor";
	
	@Inject
	@Qualifier(ModuleCommandProcessor.qualifier)
	private List<AbstractCommandProcessor> processors;
	
	public ModuleCommandProcessor() {
		super("tv", "All tv related commands");
	}
	
	@PostConstruct
	public void init() {
		this.registerProcessors(this.processors);
	}
}
```

**Note**: a parent command processor won't execute any commands. It's only used to define a hierarchy of commands.

**Note 2**: it can be as many as parent processors as wanted

Now, let's define a real command processor:

- It has to extend `AbstractCommandProcessor` 
  - with the qualifier annotation that linked it to the right parent: @Qualifier(TvCommandProcessor.qualifier)
  - The constructor takes in argument the action name which will be used in the command query, and a description for help
  - and the the method `process` that needs to be overridden to do the action.
    - this method returns a `CommandOutput` which provides a builder to build the command output (status and text if needed)

```java
@Named
@Qualifier(TvCommandProcessor.qualifier)
public class TvPoweronCommandProcessor extends AbstractCommandProcessor{

	@Inject
	private SomeTvService someTvService;

	public FreeboxListPlayersCommandProcessor() {
		super("poweron", "Power on the TV");
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		// do some actions
		CommandOutputBuilder builder = CommandOutputBuilder.build("Some text");
		builder.newLine()
            .append("Some other text on the second line with variables: {0}", "service output");
		return builder.get();
	}
}
```

The `process` method takes a `CommandRequest` as parameter. 
This object is mainly used to iterate over the query words and find the right processors.
It is also used to retrieve the command arguments when any.

First case: `tv channel -number=6`. In this example, the channel number can be retrieve by doing: `command.getIntArgument("number"")`.

Second case `tv power -on`. In this example, we will only check that the argument is defined or not, there is no need for a value: `command.hasArgument("on")`.