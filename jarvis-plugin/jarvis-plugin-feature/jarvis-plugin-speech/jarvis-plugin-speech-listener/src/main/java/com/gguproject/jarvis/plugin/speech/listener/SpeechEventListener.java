package com.gguproject.jarvis.plugin.speech.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speech.event.SpeechEventData;
import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterService;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;

import javax.inject.Named;
import java.util.List;
import java.util.Optional;

@Named
public class SpeechEventListener extends AbstractEventListener<SpeechEventData>{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpeechEventListener.class);

	private final List<AsbtractSpeechCommandProcessor> commandProcessors;

	private final InterpreterService interpreterService;

	public SpeechEventListener(List<AsbtractSpeechCommandProcessor> commandProcessors, InterpreterService interpreterService, EventBusService eventBusService) {
		super(SpeechEventData.eventType, SpeechEventData.class, eventBusService);
		this.commandProcessors = commandProcessors;
		this.interpreterService = interpreterService;
	}
	
	@Override
	public void onEvent(DistributedEvent event, SpeechEventData data) {
		try {
			Command command = this.interpreterService.interprets(data.getSpeech());
			this.processCommand(event, command);
		} catch (InterpreterException e){
			LOGGER.warn("Not able to interpret command: {} - message: {}", data.getSpeech(), e.getMessage());
		}
	}
	
	public void processCommand(DistributedEvent event, Command command) {
		Optional<AsbtractSpeechCommandProcessor> processor = this.commandProcessors.stream().filter(p -> p.canProcess(command)).findFirst();
		if(processor.isPresent()) {
			LOGGER.debug("Found processor for command: {} - {}", command, processor);
			processor.get().process(event, command);
		} else {
			LOGGER.info("No processor found for command: {}", command);
		}
	}
}
