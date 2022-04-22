package com.gguproject.jarvis.plugin.speech.interpreter;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.StringUtils;
import com.gguproject.jarvis.plugin.speech.grammar.GrammarService;
import com.gguproject.jarvis.plugin.speech.grammar.dto.GrammarContext;
import com.gguproject.jarvis.plugin.speech.interpreter.processor.*;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use the current loaded grammar to interpret a recognize speech
 * The idea is to found the action, context, data words to process
 * @author guillaumegunther
 */
@Named
public class InterpreterService {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(InterpreterService.class);

	private final GrammarService grammarService;
	
	private static List<AbstractInterpreterPreProcessor> preProcessors = new ArrayList<>();
	
	private static Map<String, AbstractInterpreterProcessor> processors = new HashMap<>();
	
	static {
		preProcessors.add(new NoisePreProcessor());
		
		processors.put(ActionProcessor.name, new ActionProcessor());
		processors.put(ContextProcessor.name, new ContextProcessor());
		processors.put(DataProcessor.name, new DataProcessor());
		processors.put(LocalizationProcessor.name, new LocalizationProcessor());
	}

	public InterpreterService(GrammarService grammarService){
		this.grammarService = grammarService;
	}

	public boolean canProcess(String speech) {
		return speech.startsWith(this.grammarService.getGrammar().getKeyword());
	}
	

	public Command interprets(String speech) throws InterpreterException {
		
		//handle speech history now!!
		
		SpeechContext context = new SpeechContext(speech);
		
		// preprocessors
		preProcessors.forEach(pp -> pp.process(this.grammarService.getGrammar(), context));
		
		// get the output synthaxe and split it
		// each part of the output may match a processor
		String output = this.grammarService.getGrammar().getGrammar().get("output");
		String[] outputParts = output.replaceAll("[\\[\\]<>]", "").split(" ");
		for(String processorName : outputParts) {
			// check if there is more words to process
			if(context.hasNext()) {
				if(processors.containsKey(processorName)) {
					processors.get(processorName).process(this.grammarService.getGrammar(), context);
				}
			}
		}
		
		List<GrammarContext> potentialContexts = context.getPotentialContexts();
		
		if(potentialContexts.isEmpty()) {
			throw new InterpreterException("No context found in speech {0}", context.getSpeech());
		} else if(StringUtils.isEmpty(context.getAction())) {
			throw new InterpreterException("No action found in speech {0}", context.getSpeech());
		} else if (potentialContexts.size() > 1){
			throw new InterpreterException("Several context has been found for speech {0}", context.getSpeech());
		}
	
		return new Command(potentialContexts.get(0).getType(), context.getAction(), context.getData(), context.getLocalization());
	}
}
