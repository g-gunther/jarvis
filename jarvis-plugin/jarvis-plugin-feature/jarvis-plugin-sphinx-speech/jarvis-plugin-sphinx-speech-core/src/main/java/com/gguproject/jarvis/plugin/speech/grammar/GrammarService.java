package com.gguproject.jarvis.plugin.speech.grammar;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.plugin.speech.grammar.dto.Grammar;

import javax.inject.Named;

/**
 * Main grammar service used to access the grammar object
 */
@Named
public class GrammarService implements OnPostConstruct {

	/**
	 * Current active grammar
	 */
	private Grammar grammar;

	private final GrammarConfigurationFileService grammarConfigurationService;

	public GrammarService(GrammarConfigurationFileService grammarConfigurationService){
		this.grammarConfigurationService = grammarConfigurationService;
	}

	@Override
	public void postConstruct() {
		this.grammar = this.grammarConfigurationService.parseConfigurationFile();
	}
	
	public Grammar getGrammar() {
		return this.grammar;
	}
}
