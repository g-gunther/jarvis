package com.gguproject.jarvis.module.core.command;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.StringUtils;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extension of {@link AbstractCommandProcessor}
 * Used to represent a step in the tree between the root and leafs.
 * It has children processors which can also be {@link AbstractCommandProcessor}
 */
public abstract class AbstractParentCommandProcessor extends AbstractCommandProcessor {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(AbstractParentCommandProcessor.class);
	
	/**
	 * List of children processors
	 */
	private final List<AbstractCommandProcessor> processors = new ArrayList<>();
	
	/**
	 * Constructor
	 * @param commandName Command name
	 * @param description Command description
	 */
	public AbstractParentCommandProcessor(String commandName, String description, List<AbstractCommandProcessor> processors) {
		super(commandName, description);
		
		// register by default the help processor
		this.registerProcessor(new AbstractCommandProcessor("help", "list all commands") {
			@Override
			public CommandOutput process(CommandRequest command) {
				CommandOutputBuilder ob = CommandOutputBuilder.build();
				AbstractParentCommandProcessor.this.listProcessorsToOutput(ob);
				return ob.get();
			}
		});
		this.registerProcessors(processors);
	}
	
	/**
	 * Register a single children processor
	 * @param processor Processor to register
	 */
	protected void registerProcessor(AbstractCommandProcessor processor) {
		this.processors.add(processor);
	}
	
	/**
	 * Empty the list of processors
	 */
	protected void clearProcessors() {
		this.processors.clear();
	}
	
	/**
	 * Register a list of children processors
	 * @param processors List of processors
	 */
	protected void registerProcessors(List<? extends AbstractCommandProcessor> processors) {
		this.processors.addAll(processors);
	}

	/**
	 * Process the command by trying to find a children processor
	 * that can handled the next command word
	 */
	@Override
	public CommandOutput process(CommandRequest command) {
		if(!command.getIterator().hasNext()) {
			CommandOutputBuilder ob = CommandOutputBuilder.build();
			this.listProcessorsToOutput(ob);
			return ob.get();
		}
		String commandWord = command.getIterator().next();
		
		AbstractCommandProcessor processor = this.findProcessorForCommand(commandWord);
		if(processor == null) {
			return CommandOutputBuilder.build().notFound().get();
		}
		
		return processor.process(command);
	}
	
	/**
	 * Try to find a children process that can handled a command word
	 * @param commandWord Command word to check
	 * @return Processor if found, null else
	 */
	private AbstractCommandProcessor findProcessorForCommand(String commandWord){
		List<AbstractCommandProcessor> foundProcessors = this.processors.stream().filter(p -> p.canProcess(commandWord)).collect(Collectors.toList());
		if(foundProcessors.size() == 1) {
			return foundProcessors.get(0);
		} else if(foundProcessors.isEmpty()){
			LOGGER.warn("Not able to find a processor for command : {}", commandWord);
		} else {
			LOGGER.warn("Several processors found for command : {}", commandWord);
		}
		return null;
	}
	
	/**
	 * Append list of processors to {@link CommandOutputBuilder}
	 * @param ob output builder
	 */
	private void listProcessorsToOutput(CommandOutputBuilder ob) {
		this.processors.forEach(p -> {
			ob.append(StringUtils.rightpad(p.getName(), 10))
				.append(" ")
				.append(p.getDescription())
				.newLine();
			if(p.hasArgumentDescription()) {
				p.listArgumentsToOutput(ob);
			}
		});
	}
}
