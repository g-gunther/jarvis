package com.gguproject.jarvis.module.core.command;

import java.util.ArrayList;
import java.util.List;

import com.gguproject.jarvis.core.utils.StringUtils;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;

/**
 * Command processor to implement
 * Each command processors is linked to a parent (except for the root one which is the main entry point)
 * and each of them handle a command word. It builds a tree of commands and classes implementing {@link AbstractCommandProcessor}
 * are leafs of that three and execute the command
 */
public abstract class AbstractCommandProcessor {

	/**
	 * Root qualitifer to identify the very first step of the command tree
	 */
	public static final String rootQualifier = "RootCommandProcessor";
	
	/**
	 * Command word
	 */
	private final String commandName;
	
	/**
	 * Description which will be displayed on help
	 */
	private final String description;
	
	/**
	 * Argument descriptions
	 */
	private final List<ArgumentDescription> argumentDescriptions = new ArrayList<>();
	
	/**
	 * Constructor
	 * @param commandName Command word
	 * @param description Command description
	 */
	public AbstractCommandProcessor(String commandName, String description) {
		this.commandName = commandName;
		this.description = description;
	}
	
	/**
	 * Indicates if this command process can handle the next command word
	 * @param command Command word
	 * @return True if ok, false else
	 */
	public boolean canProcess(String command) {
		return command.equals(this.commandName);
	}
	
	/**
	 * Method to implement which process the command and arguments
	 * @param command Command request
	 * @return Output of the command to display
	 */
	public abstract CommandOutput process(CommandRequest command);
	
	/**
	 * Get the command name
	 * @return command name
	 */
	public String getName() {
		return this.commandName;
	}
	
	/**
	 * Get command description
	 * @return Command description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Register an argument description to display on help
	 * @param argument Argument name
	 * @param description Argument description
	 */
	public void registerArgumentDescription(String argument, String description) {
		this.argumentDescriptions.add(new ArgumentDescription(argument, description));
	}
	
	/**
	 * Check if there are some argument descriptions
	 * @return True if any, false else
	 */
	public boolean hasArgumentDescription() {
		return !this.argumentDescriptions.isEmpty();
	}
	
	/**
	 * Append argument descriptions to a given {@link CommandOutputBuilder}
	 * @param ob output builder
	 */
	public void listArgumentsToOutput(CommandOutputBuilder ob) {
		this.argumentDescriptions.forEach(arg -> {
			ob.append(StringUtils.rightpad("", 10))
				.append(StringUtils.rightpad(arg.getArgument(), 10))
				.append(" ")
				.append(arg.getDescription())
				.newLine();
		});
	}
	
	/**
	 * Argument description POJO
	 */
	public class ArgumentDescription {
		
		/**
		 * Argument name
		 */
		private String argument;
		
		/**
		 * Argument description
		 */
		private String description;
		
		public ArgumentDescription(String argument, String description) {
			this.argument = argument;
			this.description = description;
		}
		
		public String getArgument() {
			return this.argument;
		}
		
		public String getDescription() {
			return this.description;
		}
	}
}
