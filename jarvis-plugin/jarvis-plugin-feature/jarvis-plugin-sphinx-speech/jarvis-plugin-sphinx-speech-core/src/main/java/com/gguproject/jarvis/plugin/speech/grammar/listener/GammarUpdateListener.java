package com.gguproject.jarvis.plugin.speech.grammar.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.plugin.speech.event.GrammarUpdateEventData;
import com.gguproject.jarvis.plugin.speech.grammar.GrammarUpdateService;

import javax.inject.Named;

/**
 * Event listener to update the grammar
 */
@Named
public class GammarUpdateListener extends AbstractEventListener<GrammarUpdateEventData> {

	private final GrammarUpdateService grammarUpdateService;

	public GammarUpdateListener(GrammarUpdateService grammarUpdateService, EventBusService eventBusService) {
		super(GrammarUpdateEventData.eventType, GrammarUpdateEventData.class, eventBusService);
		this.grammarUpdateService = grammarUpdateService;
	}
	
	@Override
	public void onEvent(DistributedEvent event, GrammarUpdateEventData data) {
		this.grammarUpdateService.update(data);
	}
}
