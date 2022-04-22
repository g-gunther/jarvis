package com.gguproject.jarvis.plugin.speech.interpreter.grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

import javax.inject.Named;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

/**
 * This service is used to parse and write the grammar configuration file
 */
@Named
public class GrammarConfigurationFileService {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(GrammarConfigurationFileService.class);
	
	private Gson gsonParser;
	
	public GrammarConfigurationFileService() {
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
				} if(json.isJsonObject()) {
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
		
		gsonBuilder.registerTypeAdapter(GrammarWord.class, new JsonDeserializer<GrammarWord>() {
			@Override
			public GrammarWord deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				return GrammarWord.build(json.getAsString());
			}
		});
				
		this.gsonParser = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();
	}
	
	/**
	 * Parse the used configuration file
	 * @return
	 */
	public Grammar parseConfigurationFile(String filename) {
		File speechFile = new File(filename);
		if(speechFile.exists()) {
			JsonReader reader;
			try {
				reader = new JsonReader(new FileReader(speechFile));
				return this.gsonParser.fromJson(reader, Grammar.class);
			} catch (FileNotFoundException e) {
				LOGGER.error("Can't find the given speech file: {}", speechFile.getName(), e);
				throw new IllegalStateException("Not able to read the speech file " + speechFile.getAbsolutePath()) ;
			}
		}
		
		// else return an empty grammar
		return new Grammar();
	}
	
	public GrammarContext parseGrammarContext(String value) {
		return this.gsonParser.fromJson(value, GrammarContext.class);
	}
}
