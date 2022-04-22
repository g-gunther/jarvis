package com.gguproject.jarvis.plugin.speech.sphinx;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.speech.grammar.GrammarConfigurationFileService;
import com.gguproject.jarvis.plugin.speech.grammar.dto.Grammar;
import com.gguproject.jarvis.plugin.speech.grammar.dto.GrammarContext;
import com.gguproject.jarvis.plugin.speech.grammar.dto.GrammarContextAction;

import javax.inject.Named;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

/**
 * This service is used to generate the sphinx grammar file
 * based on the grammar configuration file
 */
@Named
public class SphinxGrammarFileWriter {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SphinxGrammarFileWriter.class);

	private final GrammarConfigurationFileService speechConfigurationService;

	private final SpeechPluginConfiguration configuration;

	public SphinxGrammarFileWriter(GrammarConfigurationFileService speechConfigurationService, SpeechPluginConfiguration configuration){
		this.speechConfigurationService = speechConfigurationService;
		this.configuration = configuration;
	}

	/**
	 * Start the analyse of the speech configuration file
	 * Check if there is a custom configuration speech.json file under the config/ directory
	 * else process the default one in resource folder
	 */
	public Grammar generate() {
		LOGGER.info("Generate sphinx grammar based on configuration file");
		
		Grammar grammar = this.speechConfigurationService.parseConfigurationFile();
		this.generateSpeechGrammarFile(grammar);
		return grammar;
	}
	
	/**
	 * Generate the speech grammar file which will be used by sphinx for recognition
	 * @param grammar
	 */
	private void generateSpeechGrammarFile(final Grammar grammar) {
		// override the existing file
		String gramFilePath = this.configuration.getDataFilePath(this.configuration.getProperty(PropertyKey.sphinxGrammarFile));
		LOGGER.info("Target grammar file: {}", gramFilePath);
		
		try (FileWriter fw = new FileWriter(gramFilePath, false); BufferedWriter bw = new BufferedWriter(fw)) {
			bw.write("#JSGF V1.0;");
			bw.newLine();
			
			bw.write("grammar speech;");
			bw.newLine();

			bw.write("<keyword> = "+ grammar.getKeyword() +";");
			bw.newLine();
			bw.newLine();
			
			// data values
			for(Entry<String, Collection<String>> e : grammar.getData().entrySet()) {
				bw.write(this.listToString("data_" + e.getKey(), e.getValue()));
				bw.newLine();
			}
			bw.newLine();
			
			bw.write(this.listToString("localization", grammar.getLocalization().getLocalizationWords()));
			bw.newLine();
			bw.newLine();
			
			for(Entry<String, Collection<String>> e : grammar.getActions().entrySet()) {
				bw.write(this.listToString("action_" + e.getKey(), e.getValue()));
				bw.newLine();
			}
			bw.newLine();
			
			StringBuilder output = new StringBuilder("public <speech> = <keyword> (");
			
			for(GrammarContext context : grammar.getContext()) {
				bw.write(this.listToString(context.getType() + "_words", context.getWords()));
				bw.newLine();
				
				List<String> contextActionGrams = new ArrayList<>();
				for(GrammarContextAction action: context.getActions()) {
					String contextActionGram = "<" + context.getType() + "_action_" + action.getAction() + ">";
					contextActionGrams.add(contextActionGram);
					
					if(action.hasData()) {
						StringBuilder dataGram = new StringBuilder();
						for(String data : action.getData()) {
							if(data.startsWith("context:")) {
								String contextType = data.substring("context:".length());
								dataGram.append("<").append(contextType).append("_words> | ");
							} else {
								dataGram.append("<data_").append(data).append("> | ");
							}
						}
						dataGram.delete(dataGram.length() - 3, dataGram.length());
						
						bw.write(contextActionGram + " = <action_" + action.getAction() +"> [<"+ context.getType() + "_words" +">] ("+ dataGram.toString() +");");
					} else {
						bw.write(contextActionGram + " = <action_" + action.getAction() +"> [<"+ context.getType() + "_words" +">];");
					}
					
					bw.newLine();
				}
				
				bw.write(this.listToString(context.getType(), contextActionGrams));
				bw.newLine();
				bw.newLine();
				
				output.append("<").append(context.getType()).append("> | ");
			}
			
			bw.write(output.delete(output.length() - 3, output.length()).append(") [<localization>];").toString());
			
		} catch (IOException e) {
			LOGGER.error("Not able to generate the grammar file", e);
		}
	}
	
	private String listToString(String keyword, Collection<String> words) {
		StringBuilder actions = new StringBuilder();
		words.forEach(v -> {
			if(actions.length() > 0) {
				actions.append(" | ");
			}
			actions.append(String.join(" | ", v));
		});
		actions.insert(0, "<> = ").insert(1, keyword)
			.append(";");

		return actions.toString();
	}
}
