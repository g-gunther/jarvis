package com.gguproject.jarvis.plugin.freeboxv6.event.analyser;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.freeboxv6.event.InfraRedPluginConfiguration;
import com.gguproject.jarvis.plugin.freeboxv6.event.analyser.processor.CommandProcessor;
import com.gguproject.jarvis.plugin.freeboxv6.event.analyser.processor.CommandProcessorFactory;
import com.gguproject.jarvis.plugin.freeboxv6.event.analyser.processor.ShellCommandProcessorFactory;
import com.gguproject.jarvis.plugin.freeboxv6.event.analyser.processor.WaitCommandProcessorFactory;
import com.gguproject.jarvis.plugin.freeboxv6.event.event.InfraredCommand;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import javax.inject.Named;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Named
public class CommandAnalyser {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(CommandAnalyser.class);

    private final InfraRedPluginConfiguration configuration;

    private final List<CommandProcessorFactory<CommandProcessor>> commandProcessorFactories = new ArrayList<>();

    public CommandAnalyser(InfraRedPluginConfiguration configuration){
        this.configuration = configuration;
    }

    public void loadConfiguration() {
        Optional<File> file = this.configuration.getConfigurationFile(this.configuration.getProperty(InfraRedPluginConfiguration.PropertyKey.configurationFile));
        if (file.isEmpty()) {
            LOGGER.info("No command configuration file found - exit");
            return;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(CommandProcessorFactory.class, new CommandProcessorFactoryDeserializer());

        JsonReader reader;
        try {
            reader = new JsonReader(new FileReader(file.get()));
            Type listType = new TypeToken<ArrayList<CommandProcessorFactory<?>>>() {
            }.getType();
            this.commandProcessorFactories.addAll(gsonBuilder.create().fromJson(reader, listType));
            LOGGER.debug("Load {} commandProcessorFactories from configuration file: {}", commandProcessorFactories);
        } catch (FileNotFoundException e) {
            LOGGER.error("Can't load the given command file: {}", file.get().getName(), e);
            throw new IllegalStateException("Not able to load the command file " + file.get().getAbsolutePath());
        }

        this.commandProcessorFactories.add(new WaitCommandProcessorFactory());
    }

    /**
     * Analyse the context, action and data of the event and try to find
     * a shell command to execute on the server
     *
     * @param commandEventData
     * @return
     */
    public List<CommandProcessor> analyse(List<InfraredCommand> commands) {
        List<CommandProcessor> processors = new ArrayList<>();

        commands.forEach(command -> {
            CommandProcessorFactory<?> factory = this.commandProcessorFactories.stream()
                    .filter(f -> f.match(command.getContext(), command.getAction()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No factory found for context: " + command.getContext() + " and action: " + command.getAction()));

            processors.add(factory.build(command));
        });

        return processors;
//		
//		
//		
//		
//		List<Command> contextCommands = this.commands.stream().filter(c -> c.containsContextAndAction(commandEventData.getContext(), commandEventData.getAction())).collect(Collectors.toList());
//		if(contextCommands.isEmpty()) {
//			LOGGER.info("No command found for context: {} & action: {}", commandEventData.getContext(), commandEventData.getAction());
//			return null;
//		} 
//		
//		Command command;
//		if(contextCommands.size() > 1) {
//			// several commands found, try to filter with data
//			if(StringUtils.isEmpty(commandEventData.getData())) {
//				throw new IllegalStateException("Several commands found but no data for filtering");
//			}
//			List<Command> filteredCommands = contextCommands.stream().filter(c -> c.getDatas() != null && c.getDatas().contains(commandEventData.getData())).collect(Collectors.toList());
//			if(filteredCommands.isEmpty()) {
//				LOGGER.info("No command found for context: {} & action: {} & data : {}", commandEventData.getContext(), commandEventData.getAction(), commandEventData.getData());
//				return null;
//			} else if(filteredCommands.size() > 1) {
//				LOGGER.error("Several commands found for the same context: {}, action: {} & data: {}", commandEventData.getContext(), commandEventData.getAction(), commandEventData.getData());
//				throw new IllegalStateException("Several commands found for the same context, action & data");
//			}
//			command = filteredCommands.get(0);
//		} else {
//			command = contextCommands.get(0);
//		}
//		
//		if(!command.getCommands().isEmpty()) {
//			// return all processors
//			return command.getCommands();
//		}
//		
//		LOGGER.error("There is no command defined for type: {}", command);
//		throw new IllegalStateException("There is no command defined");
    }

    /**
     * Deserialize the given json into a processor factory
     */
    private class CommandProcessorFactoryDeserializer implements JsonDeserializer<CommandProcessorFactory<?>> {
        public CommandProcessorFactory<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext c) {
            String context = json.getAsJsonObject().get("context").getAsString();
            String action = json.getAsJsonObject().get("action").getAsString();
            String command = json.getAsJsonObject().get("command").getAsString();

            return new ShellCommandProcessorFactory(context, action, command);
        }
    }
}
