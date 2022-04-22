package com.gguproject.jarvis.module.core.command.support;

import com.gguproject.jarvis.module.core.command.support.CommandOutput.CommandOutputStatus;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Command output build 
 * {@link CommandOutput}
 */
public class CommandOutputBuilder {

	/**
	 * Get a new instance of the builder
	 * @return Instance of builder
	 */
	public static CommandOutputBuilder build() {
		return new CommandOutputBuilder();
	}
	
	/**
	 * Get a new instance of the builder
	 * @param value First line of the output
	 * @param args Argument to replace in the line
	 * @return Insance of builder
	 */
	public static CommandOutputBuilder build(String value, Object...args) {
		return new CommandOutputBuilder().append(value, args);
	}
	
	/**
	 * List of lines
	 */
	private final List<String> lines = new ArrayList<>();
	
	/**
	 * Current line builder
	 */
	private final StringBuilder sb = new StringBuilder();
	
	/**
	 * Output status
	 */
	private CommandOutputStatus status = CommandOutputStatus.SUCCESS;
	
	public CommandOutputBuilder() {
	}
	
	/**
	 * constructor
	 * @param value Initial value
	 */
	public CommandOutputBuilder(String value) {
		this.append(value);
	}
	
	/**
	 * Append a new value to the first line
	 * @param value Value
	 * @param args Arguments to replace in the value
	 * @return Instance of builder
	 */
	public CommandOutputBuilder append(String value, Object...args) {
		this.sb.append(MessageFormat.format(value, args));
		return this;
	}
	
	/**
	 * Create a new line and reset the current line
	 * @return Instance of builder
	 */
	public CommandOutputBuilder newLine() {
		this.lines.add(this.sb.toString());
		this.sb.setLength(0);
		return this;
	}
	
	/**
	 * Set the output status
	 * @param status
	 * @return
	 */
	public CommandOutputBuilder status(CommandOutputStatus status) {
		this.status = status;
		return this;
	}
	
	public CommandOutputBuilder error() {
		return this.status(CommandOutputStatus.ERROR);
	}
	
	public CommandOutputBuilder notFound() {
		return this.status(CommandOutputStatus.NOTFOUND);
	}
	
	/**
	 * Generate the {@link CommandOutput}
	 * @return Command output
	 */
	public CommandOutput get(){
		if(this.sb.length() > 0) {
			this.newLine();
		}
		return new CommandOutput(this.status, this.lines);
	}
}
