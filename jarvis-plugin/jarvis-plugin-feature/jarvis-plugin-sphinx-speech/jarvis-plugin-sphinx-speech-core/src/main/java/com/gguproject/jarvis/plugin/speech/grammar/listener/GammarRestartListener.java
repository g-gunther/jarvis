package com.gguproject.jarvis.plugin.speech.grammar.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.plugin.speech.event.GrammarRestartEventData;
import com.gguproject.jarvis.plugin.speech.recognizer.SpeechRecognitionManager;

import javax.inject.Named;

/**
 * Event listener to restart the speech recognition engine
 */
@Named
public class GammarRestartListener extends AbstractEventListener<GrammarRestartEventData> {

	private final SpeechRecognitionManager speechRecognitionManager;

	public GammarRestartListener(SpeechRecognitionManager speechRecognitionManager, EventBusService eventBusService) {
		super(GrammarRestartEventData.eventType, GrammarRestartEventData.class, eventBusService);
		this.speechRecognitionManager = speechRecognitionManager;
	}
	
	@Override
	public void onEvent(DistributedEvent event, GrammarRestartEventData data) {
		this.speechRecognitionManager.restart();
	}
}
