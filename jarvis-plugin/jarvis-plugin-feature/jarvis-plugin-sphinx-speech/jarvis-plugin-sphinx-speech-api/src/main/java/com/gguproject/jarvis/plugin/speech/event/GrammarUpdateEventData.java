package com.gguproject.jarvis.plugin.speech.event;

import com.gguproject.jarvis.core.bus.support.EventData;

public class GrammarUpdateEventData extends EventData {
	private static final long serialVersionUID = -8017535104899632667L;
	public static final String eventType = "SPHINX_GRAMMAR_UPDATE";
	
	private Action action;
	
	private TargetElement targetElement;
	
	private String entry;
	
	private String value;
	
	public GrammarUpdateEventData() {
		super(eventType, GrammarUpdateEventData.class);
	}
	
	public GrammarUpdateEventData(Action action, TargetElement targetElement, String entry, String value) {
		this();
		this.action = action;
		this.targetElement = targetElement;
		this.entry = entry;
		this.value = value;
	}
	
	public Action getAction() {
		return this.action;
	}
	
	public TargetElement getTargetElement() {
		return this.targetElement;
	}
	
	public String getEntry() {
		return this.entry;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public enum Action {
		ADD,
		REMOVE;
	}
	
	public enum TargetElement {
		KEYWORD,
		DATA,
		ACTION,
		LOCALIZATION,
		NOISE,
		CONTEXT,
		GRAMMAR_DEFINITION,
		OUTPUT_DEFINITION;
	}
}
