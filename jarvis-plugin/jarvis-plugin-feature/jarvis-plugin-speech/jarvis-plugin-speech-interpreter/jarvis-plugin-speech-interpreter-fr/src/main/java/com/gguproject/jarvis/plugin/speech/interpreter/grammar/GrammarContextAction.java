package com.gguproject.jarvis.plugin.speech.interpreter.grammar;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a context action
 */
public class GrammarContextAction {

	/**
	 * Action type name
	 */
	private String action;
	
	/**
	 * List of data needed for this action
	 */
	private Set<String> dataType = new HashSet<String>();
	
	public GrammarContextAction(String action, String...dataType) {
		this.action = action;
		this.dataType.addAll(Arrays.asList(dataType));
	}
	
	/**
	 * Add a new data value
	 * @param data
	 */
	public void addData(String dataType) {
		this.dataType.add(dataType);
	}
	
	/**
	 * Add a list of data
	 * @param data
	 */
	public void addData(Collection<String> dataType) {
		this.dataType.addAll(dataType);
	}

	/**
	 * Return the action name
	 * @return
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * Return the data list
	 * @return
	 */
	public Collection<String> getData() {
		return Collections.unmodifiableSet(dataType);
	}
	
	/**
	 * Indicates if there are some data
	 * @return
	 */
	public boolean hasData() {
		return !this.dataType.isEmpty();
	}
	/**
	 * Check if the action contains a data
	 * @param word
	 * @return
	 */
	public boolean containsDataType(String datatype) {
		return this.dataType.contains(datatype);
	}

	@Override
	public String toString() {
		return "GrammarContextAction [action=" + action + ", data=" + dataType + "]";
	}
}
