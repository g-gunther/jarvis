package com.gguproject.jarvis.plugin.speech.grammar;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speech.event.GrammarUpdateEventData;
import com.gguproject.jarvis.plugin.speech.event.GrammarUpdateEventData.Action;
import com.gguproject.jarvis.plugin.speech.event.GrammarUpdateEventData.TargetElement;
import com.gguproject.jarvis.plugin.speech.grammar.dto.Grammar;
import com.gguproject.jarvis.plugin.speech.grammar.dto.GrammarContext;
import com.gguproject.jarvis.plugin.speech.recognizer.SpeechRecognitionManager;
import com.gguproject.jarvis.plugin.speech.sphinx.SphinxDictionnaryException;
import com.gguproject.jarvis.plugin.speech.sphinx.SphinxDictionnaryService;
import com.gguproject.jarvis.plugin.speech.sphinx.SphinxGrammarFileWriter;

import javax.inject.Named;
import java.util.*;

/**
 * This service is used to update the grammar configuration
 * by adding or removing words
 */
@Named
public class GrammarUpdateService {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(GrammarUpdateService.class);

	private final GrammarService  grammarService;

	private final SpeechRecognitionManager speechRecognitionManager;

	private final GrammarConfigurationFileService  grammarConfigurationFileService;

	private final SphinxDictionnaryService sphinxDictionnaryService;

	private final SphinxGrammarFileWriter sphinxGrammarFileWriter;

	private Map<GrammarUpdateProcessorKey, GrammarUpdateProcessor> processors = new HashMap<>();

	public GrammarUpdateService(GrammarService  grammarService, SpeechRecognitionManager speechRecognitionManager,
								GrammarConfigurationFileService  grammarConfigurationFileService, SphinxDictionnaryService sphinxDictionnaryService,
								SphinxGrammarFileWriter sphinxGrammarFileWriter) {
		this.grammarService = grammarService;
		this.speechRecognitionManager = speechRecognitionManager;
		this.grammarConfigurationFileService = grammarConfigurationFileService;
		this.sphinxDictionnaryService = sphinxDictionnaryService;
		this.sphinxGrammarFileWriter = sphinxGrammarFileWriter;

		this.processors.put(new GrammarUpdateProcessorKey(Action.ADD, TargetElement.KEYWORD), (Grammar grammar, GrammarUpdateEventData data) -> {
			try {
				sphinxDictionnaryService.addWordsInDictionnary(data.getValue());
				this.removeWordsFromDictionnary(grammar, data.getValue());
			} catch (SphinxDictionnaryException e) {
				throw new GrammarUpdateException("Could not add/remove keywords in dictionnary: " + data.getValue(), e) ;
			}
			grammar.setKeyword(data.getValue());
		});
		
		this.processors.put(new GrammarUpdateProcessorKey(Action.ADD, TargetElement.CONTEXT), (Grammar grammar, GrammarUpdateEventData data) -> {
			GrammarContext context = this.grammarConfigurationFileService.parseGrammarContext(data.getValue());
			try {
				sphinxDictionnaryService.addWordsInDictionnary(context.getWords());
			} catch (SphinxDictionnaryException e) {
				throw new GrammarUpdateException("Could not add words in dictionnary: " + context.getWords(), e) ;
			}
			grammar.addContext(context);
		});
		this.processors.put(new GrammarUpdateProcessorKey(Action.REMOVE, TargetElement.CONTEXT), (Grammar grammar, GrammarUpdateEventData data) -> {
			Optional<GrammarContext> context = grammar.getContext().stream().filter(c -> c.getType().equals(data.getEntry())).findFirst();
			if(context.isPresent()) {
				try {
					this.removeWordsFromDictionnary(grammar, context.get().getWords());
				} catch (SphinxDictionnaryException e) {
					throw new GrammarUpdateException("Could not add words in dictionnary: " + context.get().getWords(), e) ;
				}
				grammar.removeContext(data.getEntry());
			} else {
				throw new GrammarUpdateException("Context: " + data.getEntry() + " does not exist in configuration") ;
			}
			// remove context words from dictionnary
		});
		
		//
		this.putAddProcessor(Action.ADD,  TargetElement.DATA, (Grammar grammar, GrammarUpdateEventData data) -> grammar.addData(data.getEntry(), data.getValue()));
		this.putRemoveProcessor(Action.REMOVE,  TargetElement.DATA, (Grammar grammar, GrammarUpdateEventData data) -> grammar.removeData(data.getEntry(), data.getValue()));
		
		this.putAddProcessor(Action.ADD, TargetElement.ACTION, (Grammar grammar, GrammarUpdateEventData data) -> grammar.addAction(data.getEntry(), data.getValue()));
		this.putRemoveProcessor(Action.REMOVE, TargetElement.ACTION, (Grammar grammar, GrammarUpdateEventData data) -> grammar.removeAction(data.getEntry(), data.getValue()));
		
		this.putAddProcessor(Action.ADD, TargetElement.NOISE, (Grammar grammar, GrammarUpdateEventData data) -> grammar.addNoise(data.getEntry(), data.getValue()));
		this.putRemoveProcessor(Action.REMOVE, TargetElement.NOISE, (Grammar grammar, GrammarUpdateEventData data) -> grammar.removeNoise(data.getEntry(), data.getValue()));
		
		this.putAddProcessor(Action.ADD, TargetElement.LOCALIZATION, (Grammar grammar, GrammarUpdateEventData data) -> grammar.getLocalization().addLocalization(data.getEntry(), data.getValue()));
		this.putRemoveProcessor(Action.REMOVE, TargetElement.LOCALIZATION, (Grammar grammar, GrammarUpdateEventData data) -> grammar.getLocalization().removeLocalization(data.getEntry(), data.getValue()));
		
		// no dictionnary updates
		this.processors.put(new GrammarUpdateProcessorKey(Action.ADD, TargetElement.GRAMMAR_DEFINITION), (Grammar grammar, GrammarUpdateEventData data) -> grammar.addGrammar(data.getEntry(), data.getValue()));
		this.processors.put(new GrammarUpdateProcessorKey(Action.REMOVE, TargetElement.GRAMMAR_DEFINITION), (Grammar grammar, GrammarUpdateEventData data) -> grammar.removeGrammar(data.getEntry()));
		this.processors.put(new GrammarUpdateProcessorKey(Action.ADD, TargetElement.OUTPUT_DEFINITION), (Grammar grammar, GrammarUpdateEventData data) -> grammar.updateOutputDefinition(data.getValue()));
	}
	
	/**
	 * Update the grammar with the given event data
	 * @param data
	 * @return
	 */
	public boolean update(GrammarUpdateEventData data) {
		GrammarUpdateProcessorKey key = new GrammarUpdateProcessorKey(data.getAction(), data.getTargetElement());
		if(this.processors.containsKey(key)) {
			// read the grammar, update it and write it
			Grammar grammar = this.grammarConfigurationFileService.parseConfigurationFile();
			try {
				this.processors.get(key).process(grammar, data);
			} catch (GrammarUpdateException e) {
				LOGGER.error("An error occurs while updating the grammar with data: {}", data, e);
				return false;
			}
			
			this.grammarConfigurationFileService.writeConfigurationFile(grammar);
			
			// reload the main grammar object
			this.grammarService.postConstruct();
			
			// generate the sphinx grammar file
			this.sphinxGrammarFileWriter.generate();
			
			// and the speech recognition thread
			this.speechRecognitionManager.restart();
			return true;
		} else {
			LOGGER.error("Can't find any grammar update processor for data: {}", data);
			return false;
		}
	}
	
	private void removeWordsFromDictionnary(Grammar grammar, String sentence) throws SphinxDictionnaryException {
		Collection<String> words = this.sphinxDictionnaryService.parseSentenceToWords(sentence);
		this.removeWordsFromDictionnary(grammar, words);
	}
	
	/**
	 * Check that words to remove are only used from this entry 
	 * if not, let them, else remove them
	 * @param words
	 * @throws SphinxDictionnaryException
	 */
	private void removeWordsFromDictionnary(Grammar grammar, Collection<String> words) throws SphinxDictionnaryException {
		Collection<String> existingWords = grammar.getAllWords();
		List<String> wordsToRemove = new ArrayList<>();
		for(String word : words) {
			long nbWords = existingWords.stream().filter(w -> w.equals(word)).count();
			if(nbWords == 1) {
				wordsToRemove.add(word);
			} else {
				LOGGER.info("Word {} has not been removed because used several times");
			}
		}
		
		this.sphinxDictionnaryService.removeWordsFromDictionnary(wordsToRemove);
	}
	
	
	private void putAddProcessor(Action action, TargetElement target, GrammarUpdateProcessor processor) {
		this.processors.put(new GrammarUpdateProcessorKey(action, target), new GrammarAndDictionnaryAddProcessor(processor));
	}
	
	private void putRemoveProcessor(Action action, TargetElement target, GrammarUpdateProcessor processor) {
		this.processors.put(new GrammarUpdateProcessorKey(action, target), new GrammarAndDictionnaryRemoveProcessor(processor));
	}
	
	/**
	 * Key of grammar update processor
	 */
	private class GrammarUpdateProcessorKey{
		private Action action;
		private TargetElement targetElement;
		
		public GrammarUpdateProcessorKey(Action action, TargetElement targetElement) {
			this.action = action;
			this.targetElement = targetElement;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((action == null) ? 0 : action.hashCode());
			result = prime * result + ((targetElement == null) ? 0 : targetElement.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GrammarUpdateProcessorKey other = (GrammarUpdateProcessorKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (action != other.action)
				return false;
			if (targetElement != other.targetElement)
				return false;
			return true;
		}

		private GrammarUpdateService getOuterType() {
			return GrammarUpdateService.this;
		}
	}
	
	/**
	 * Define a processor
	 */
	@FunctionalInterface
	private interface GrammarUpdateProcessor {
		public void process(Grammar grammar, GrammarUpdateEventData data) throws GrammarUpdateException;
	}
	
	/**
	 * Update the grammar configuration and add words to the dictionnary
	 */
	private class GrammarAndDictionnaryAddProcessor implements GrammarUpdateProcessor {
		
		private GrammarUpdateProcessor processor;
		
		public GrammarAndDictionnaryAddProcessor(GrammarUpdateProcessor processor) {
			this.processor = processor;
		}
		
		public void process(Grammar grammar, GrammarUpdateEventData data) throws GrammarUpdateException {
			try {
				sphinxDictionnaryService.addWordsInDictionnary(data.getValue());
			} catch (SphinxDictionnaryException e) {
				throw new GrammarUpdateException("Could not add words in dictionnary: " + data.getValue(), e) ;
			}
			
			this.processor.process(grammar, data);
		}
	}
	
	/**
	 * Update the grammar configuration and remove words from the dictionnary
	 */
	private class GrammarAndDictionnaryRemoveProcessor implements GrammarUpdateProcessor {
		
		private GrammarUpdateProcessor processor;
		
		public GrammarAndDictionnaryRemoveProcessor(GrammarUpdateProcessor processor) {
			this.processor = processor;
		}
		
		public void process(Grammar grammar, GrammarUpdateEventData data) throws GrammarUpdateException {
			try {
				removeWordsFromDictionnary(grammar, data.getValue());
			} catch (SphinxDictionnaryException e) {
				throw new GrammarUpdateException("Could not remove words in dictionnary: " + data.getValue(), e) ;
			}
			
			this.processor.process(grammar, data);
		}
	}
}
