package com.gguproject.jarvis.plugin.time.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.support.CommandOutput;
import com.gguproject.jarvis.module.core.command.support.CommandOutputBuilder;
import com.gguproject.jarvis.module.core.command.support.CommandRequest;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Time;
import com.gguproject.jarvis.plugin.speech.interpreter.support.TimeUnit;
import com.gguproject.jarvis.plugin.speech.listener.SpeechEventListener;

import javax.inject.Named;

@Named
@Qualifier(TimerCommandProcessor.qualifier)
public class TimerSetCommandProcessor extends AbstractCommandProcessor{
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(TimerSetCommandProcessor.class);

	private final SpeechEventListener speechEventListener;

	public TimerSetCommandProcessor(SpeechEventListener speechEventListener) {
		super("set", "Set a timer");
		this.speechEventListener = speechEventListener;
	}
	
	@Override
	public CommandOutput process(CommandRequest command) {
		var hour = command.getIntArgumentOrElse("hour", 0);
		var minute = command.getIntArgumentOrElse("minute", 0);
		var second = command.getIntArgumentOrElse("second", 0);
		var exactTime = command.getBoolArgumentOrElse("exact", false);
		
		Time timeBuilder = Time.build();
		if(hour > 0) { timeBuilder.add(TimeUnit.HOUR, hour); }
		if(minute > 0) { timeBuilder.add(TimeUnit.MINUTE, minute); }
		if(second > 0) { timeBuilder.add(TimeUnit.SECOND, second); }
		if(exactTime) { timeBuilder.setExactTime(); }
		
		Command timerCommand = Command.builder("TIMER", "SET").time(timeBuilder).build();
		
		// need to process it as a speech command in order to handle delayed commands
		speechEventListener.processCommand(null, timerCommand);
		
		return CommandOutputBuilder.build("Command executed").get();
	}
}
