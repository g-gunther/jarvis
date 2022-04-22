package com.gguproject.jarvis.plugin.speech.grammar.command;

import com.gguproject.jarvis.core.ioc.context.annotation.Qualifier;
import com.gguproject.jarvis.module.core.command.AbstractCommandProcessor;
import com.gguproject.jarvis.module.core.command.AbstractParentCommandProcessor;

import javax.inject.Named;
import java.util.List;

/**
 * Main grammar command entry point
 */
@Named
@Qualifier(AbstractCommandProcessor.rootQualifier)
public class GrammarCommandProcessor extends AbstractParentCommandProcessor{
	public static final String qualifier = "GrammarCommandProcessor";

	public GrammarCommandProcessor(@Qualifier(GrammarCommandProcessor.qualifier) List<AbstractCommandProcessor> processors) {
		super("gram", "All grammar related commands", processors);
	}
}
