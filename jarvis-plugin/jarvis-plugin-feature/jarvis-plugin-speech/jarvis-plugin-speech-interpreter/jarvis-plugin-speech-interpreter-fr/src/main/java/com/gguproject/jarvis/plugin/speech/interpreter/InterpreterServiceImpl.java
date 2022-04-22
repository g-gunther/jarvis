package com.gguproject.jarvis.plugin.speech.interpreter;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;
import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.utils.StringUtils;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.GrammarConfigurationFileService;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.GrammarContext;
import com.gguproject.jarvis.plugin.speech.interpreter.processor.*;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;

import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The idea is to found the action, context, data words to process
 * based on the given grammar
 * @author guillaumegunther
 */
@Named
public class InterpreterServiceImpl implements InterpreterService, OnPostConstruct {

	// linked because the order of processors matters
	private static Map<String, AbstractInterpreterProcessor> processors = new LinkedHashMap<>();
	
	static {
		/*
		 * first process the timer processor to find if the command has to be delayed
		 */
		processors.put(TimeProcessor.name, new TimeProcessor());
		/*
		 * Then the localization which will check the last word of the speech and if it matches to 
		 * a known localization 
		 */
		processors.put(LocalizationProcessor.name, new LocalizationProcessor());
		/*
		 * Then the action which is determines by the verbs
		 */
		processors.put(ActionProcessor.name, new ActionProcessor());
		/*
		 * Then the context on which the action is applied to (music, tv...)
		 */
		processors.put(ContextProcessor.name, new ContextProcessor());
		/*
		 * And finally the remaining words which can be used to specify the action
		 * (tv channel, playlist...)
		 * Should be the last because all the remaining words are considered to be part of the data block
		 */
		//to update
		processors.put(DataProcessor.name, new DataProcessor());
	}

	private final GrammarConfigurationFileService grammarConfiguration;
	
	private static final String grammarFile = "grammar.json";

	private final AbstractPluginConfiguration configuration;
	
	private Grammar grammar;

	public InterpreterServiceImpl(GrammarConfigurationFileService grammarConfiguration, AbstractPluginConfiguration configuration){
		this.grammarConfiguration = grammarConfiguration;
		this.configuration = configuration;
	}

	@Override
	public void postConstruct() {
		this.grammar = this.grammarConfiguration.parseConfigurationFile(this.configuration.getDataFilePath(grammarFile));
	}

	@Override
	public Command interprets(String speech) throws InterpreterException {
		//TODO: handle speech history now!!
		
		SpeechContext context = new SpeechContext(speech);
		
		for(AbstractInterpreterProcessor processor: processors.values()){
			processor.process(this.grammar, context);
		}
		
		List<GrammarContext> potentialContexts = context.getPotentialContexts();
		
		if(potentialContexts.isEmpty()) {
			throw new InterpreterException("No context found in speech {0}", context.getSpeech());
		} else if(StringUtils.isEmpty(context.getAction())) {
			throw new InterpreterException("No action found in speech {0}", context.getSpeech());
		} else if (potentialContexts.size() > 1){
			throw new InterpreterException("Several context has been found for speech {0}", context.getSpeech());
		}
	
		return Command.builder(potentialContexts.get(0).getType(), context.getAction())
				.data(context.getData())
				.localizations(context.getLocalizations())
				.time(context.getTime())
				.build();
	}
	
	@Override
	public String cleanDataSpeech(String data) {
		String firstWord = data.substring(0, data.indexOf(" "));
		if(this.grammar.getAllSmallWords().contains(firstWord)) {
			return data.substring(data.indexOf(" ") + 1);
		} 
		return data;
	}
}
