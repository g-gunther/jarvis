package com.gguproject.jarvis.plugin.speech.event;

import com.gguproject.jarvis.core.bus.support.EventData;

public class GrammarRestartEventData extends EventData {
	private static final long serialVersionUID = -8017535104899632667L;
	public static final String eventType = "SPHINX_GRAMMAR_RESTART";
	
	public GrammarRestartEventData() {
		super(eventType, GrammarRestartEventData.class);
	}
}
