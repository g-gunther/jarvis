package com.gguproject.jarvis.plugin.speech.grammar.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.speech.recognizer.SpeechRecognitionManager;

import javax.inject.Named;

/**
 * Command processor to restart the recognition engine
 */
@Named
@Qualifier(GrammarCommandProcessor.qualifier)
public class GrammarRestartCommandProcessor extends AbstractCommandProcessor {

	private final SpeechRecognitionManager speechRecognitionManager;

	public GrammarRestartCommandProcessor(SpeechRecognitionManager speechRecognitionManager) {
		super("restart", "Restart the speech recognition engine");
		this.speechRecognitionManager = speechRecognitionManager;
	}

	@Override
	public CommandOutput process(CommandRequest command) {
		this.speechRecognitionManager.restart();
		return CommandOutputBuilder.build("Speech engine has been restarted").get();
	}
}