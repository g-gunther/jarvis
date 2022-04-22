package com.gguproject.jarvis.plugin.speech.grammar;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.speech.grammar.dto.Grammar;
import com.gguproject.jarvis.plugin.speech.grammar.dto.GrammarContext;
import com.gguproject.jarvis.plugin.speech.grammar.dto.GrammarContextAction;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import javax.inject.Named;
import java.io.*;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * This service is used to parse and write the grammar configuration file
 */
@Named
public class GrammarConfigurationFileService {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(GrammarConfigurationFileService.class);

	private final SpeechPluginConfiguration configuration;
	
	private Gson gsonParser;
	
	public GrammarConfigurationFileService(SpeechPluginConfiguration configuration) {
		this.configuration = configuration;

		GsonBuilder gsonBuilder = new GsonBuilder();
		
		/*
		 * json reader parser 
		 * allow the context action to be a simple string (action) or an object containing the action & list of data
		 */
		gsonBuilder.registerTypeAdapter(GrammarContextAction.class, new JsonDeserializer<GrammarContextAction>() {
			@Override
			public GrammarContextAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
				if(json.isJsonPrimitive()) {
					return new GrammarContextAction(json.getAsString());
				} else if(json.isJsonObject()) {
					GrammarContextAction action = new GrammarContextAction(json.getAsJsonObject().get("action").getAsString());
					json.getAsJsonObject().get("data").getAsJsonArray().forEach(elmt -> {
						action.addData(elmt.getAsString());
					});
					return action;
				} else {
					LOGGER.error("Not able to read json element as grammar action: {}", json);
				}
				return null;
			}
		});
		/*
		 * Json writer serializer
		 * If the contact action contains only an action it returns a simple string, else an object
		 */
		gsonBuilder.registerTypeAdapter(GrammarContextAction.class, new JsonSerializer<GrammarContextAction>() {

			@Override
			public JsonElement serialize(GrammarContextAction src, Type typeOfSrc, JsonSerializationContext context) {
				if(src.getData().isEmpty()) {
					return new JsonPrimitive(src.getAction());
				} else {
					JsonObject o = new JsonObject();
					o.addProperty("action", src.getAction());
					JsonArray a = new JsonArray();
					src.getData().forEach(d -> a.add(d));
					o.add("data", a);
					return o;
				}
			}
			
		});
				
		this.gsonParser = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();
	}
	
	/**
	 * Parse the used configuration file
	 * @return
	 */
	public Grammar parseConfigurationFile() {
		Optional<File> speechFile = this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.configuration));

		if(speechFile.isPresent()) {
			JsonReader reader;
			try {
				reader = new JsonReader(new FileReader(speechFile.get()));
				return this.gsonParser.fromJson(reader, Grammar.class);
			} catch (FileNotFoundException e) {
				LOGGER.error("Can't find the given speech file: {}", speechFile.get().getName(), e);
				throw new IllegalStateException("Not able to read the speech file " + speechFile.get().getAbsolutePath()) ;
			}
		}
		
		// else return an empty grammar
		return new Grammar();
	}
	
	public GrammarContext parseGrammarContext(String value) {
		return this.gsonParser.fromJson(value, GrammarContext.class);
	}
	
	/**
	 * Write the configuration file
	 * @param grammar
	 */
	public void writeConfigurationFile(Grammar grammar) {
		File speechFile = this.configuration.getConfigurationFileOrCreate(this.configuration.getProperty(PropertyKey.configuration));

		try (Writer writer = new FileWriter(speechFile)) {
			this.gsonParser.toJson(grammar, writer);
		} catch (IOException e) {
			LOGGER.error("Can't write the configuration file: {}", speechFile.getName(), e);
			throw new IllegalStateException("Not able to write the speech file " + speechFile.getAbsolutePath()) ;
		}
	}
}
