package com.gguproject.jarvis.plugin.speech.grammar.dto;

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
	private Set<String> data = new HashSet<String>();
	
	public GrammarContextAction(String action) {
		this.action = action;
	}
	
	/**
	 * Add a new data value
	 * @param data
	 */
	public void addData(String data) {
		this.data.add(data);
	}
	
	/**
	 * Add a list of data
	 * @param data
	 */
	public void addData(Collection<String> data) {
		this.data.addAll(data);
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
		return Collections.unmodifiableSet(data);
	}
	
	/**
	 * Indicates if there are some data
	 * @return
	 */
	public boolean hasData() {
		return !this.data.isEmpty();
	}
	/**
	 * Check if the action contains a data
	 * @param word
	 * @return
	 */
	public boolean containsData(String word) {
		return this.data.contains(word);
	}

	@Override
	public String toString() {
		return "GrammarContextAction [action=" + action + ", data=" + data + "]";
	}
}
