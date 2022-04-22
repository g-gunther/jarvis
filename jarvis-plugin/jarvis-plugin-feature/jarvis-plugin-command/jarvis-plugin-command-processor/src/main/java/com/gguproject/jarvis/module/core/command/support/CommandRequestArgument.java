package com.gguproject.jarvis.module.core.command.support;

import com.gguproject.jarvis.core.utils.StringUtils;

/**
 * POJO to represents a command argument
 * Used during the parsing of the command
 */
public class CommandRequestArgument {
	/**
	 * Argument name
	 */
	private String name;
	
	/**
	 * Argument value
	 */
	private StringBuilder value = new StringBuilder();
	
	/**
	 * Initialization of the argument with no value
	 * @param name Argument name
	 */
	public void init(String name) {
		this.name = name;
		this.value.setLength(0);
	}
	
	/**
	 * Initialization of the argument
	 * @param name Argument name
	 * @param value Argument value
	 */
	public void init(String name, String value) {
		this.init(name);
		this.value.append(value);
	}
	
	/**
	 * Append argument value (used if the argument value is composed of spaces & words)
	 * @param value Argument value
	 */
	public void append(String value) {
		this.value.append(" ").append(value);
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value.toString();
	}
	
	public boolean isEmpty() {
		return StringUtils.isEmpty(this.name);
	}
}