package com.gguproject.jarvis.plugin.time.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.listener.SpeechEventListener;

import javax.inject.Named;

@Named
@Qualifier(TimerCommandProcessor.qualifier)
public class TimerStopCommandProcessor extends AbstractCommandProcessor{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(TimerStopCommandProcessor.class);

	private final SpeechEventListener speechEventListener;

	public TimerStopCommandProcessor(SpeechEventListener speechEventListener) {
		super("stop", "Stop a timer");
		this.speechEventListener = speechEventListener;
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		Command timerCommand = Command.builder("TIMER", "STOP").build();
		speechEventListener.processCommand(null, timerCommand);
		return CommandOutputBuilder.build("Command executed").get();
	}
}
