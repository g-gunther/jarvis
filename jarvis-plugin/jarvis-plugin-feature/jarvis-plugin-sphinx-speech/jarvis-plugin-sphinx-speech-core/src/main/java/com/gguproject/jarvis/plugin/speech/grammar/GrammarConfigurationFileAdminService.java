package com.gguproject.jarvis.plugin.speech.grammar;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration.PropertyKey;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import javax.inject.Named;
import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This service is used to update the grammar configuration file
 * at the finest level. It enables us to update any nodes by any values of the json object
 */
@Named
public class GrammarConfigurationFileAdminService {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(GrammarConfigurationFileAdminService.class);

	private final SpeechPluginConfiguration configuration;
	
	private JsonParser jsonParser = new JsonParser();
	
	private static final Pattern treeEntryPattern = Pattern.compile("(\\w+)(\\[(\\w+)=(\\w+)\\])?");

	public GrammarConfigurationFileAdminService(SpeechPluginConfiguration configuration){
		this.configuration = configuration;
	}

	/**
	 * Add the value to the node that match the query
	 * @param entry
	 * @param value
	 * @throws GrammarConfigurationParseException 
	 */
	public void addEntry(String entry, String value) throws GrammarConfigurationParseException {
		File speechFile = this.configuration.getConfigurationFileOrCreate(this.configuration.getProperty(PropertyKey.configuration));
		JsonReader reader;
		
		try {
			// read the file and get the root element
			reader = new JsonReader(new FileReader(speechFile));
			JsonParser parser = new JsonParser();
			JsonElement root = parser.parse(reader);
			
			// Find the array of values to edit
			JsonArray values = this.findNode(root, entry);
			
			// convert to json the value to add
			JsonElement elmtToAdd = this.jsonParser.parse(value);
			
			// if the element to add is an object, extract primitive properties
			// which will be used to check if the value is already there or not
			Map<String, String> elmtToAddObjIds = null;
			if(elmtToAdd.isJsonObject()) {
				elmtToAddObjIds = this.getPrimitiveProperties(elmtToAdd.getAsJsonObject());
			}
			
			// check if the value is already in the array or not
			Iterator<JsonElement> it = values.iterator();
			while(it.hasNext()) {
				JsonElement elmt = it.next();
				// if the element is an object, then check all primitive properties
				if(elmt.isJsonObject()) {
					Map<String, String> elmtObjIds = this.getPrimitiveProperties(elmt.getAsJsonObject());
					if(elmtObjIds.equals(elmtToAddObjIds)) {
						throw new GrammarConfigurationParseException("The value '" + value + "' is alread in values for query '" + entry + "'");
					}
				} 
				// else get the value as string
				else {
					if(elmt.getAsString().equals(value)) {
						throw new GrammarConfigurationParseException("The value '" + value + "' is alread in values for query '" + entry + "'");
					}
				}
			}
			
			// add the value depending if it's a json object or a simple value
			if(elmtToAdd.isJsonObject()) {
				values.add(elmtToAdd);
			} else {
				values.add(value);	
			}
			
			// and finally write the file
			this.writeConfigurationFile(speechFile, root);
		} catch (FileNotFoundException e) {
			throw new GrammarConfigurationParseException("Speech file not found", e);
		}
	}
	
	/**
	 * Remove the value from the node that match the query
	 * @param entry
	 * @param value
	 * @throws GrammarConfigurationParseException 
	 */
	public void removeEntry(String entry, String value) throws GrammarConfigurationParseException {
		File speechFile = this.configuration.getConfigurationFileOrCreate(this.configuration.getProperty(PropertyKey.configuration));

		JsonReader reader;
		try {
			// read the file and get the root element
			reader = new JsonReader(new FileReader(speechFile));
			JsonParser parser = new JsonParser();
			JsonElement root = parser.parse(reader);
			
			JsonArray values = this.findNode(root, entry);
			
			JsonElement elmtToRemove = this.jsonParser.parse(value);
			
			// if the element to add is an object, extract primitive properties
			// which will be used to check if the value is already there or not
			Map<String, String> elmtToRemoveObjIds = null;
			if(elmtToRemove.isJsonObject()) {
				elmtToRemoveObjIds = this.getPrimitiveProperties(elmtToRemove.getAsJsonObject());
			}
			
			// check if the value is already in the array or not
			Iterator<JsonElement> it = values.iterator();
			boolean found = false;
			while(it.hasNext()) {
				JsonElement elmt = it.next();
				if(elmt.isJsonObject()) {
					Map<String, String> elmtObjIds = this.getPrimitiveProperties(elmt.getAsJsonObject());
					if(elmtObjIds.equals(elmtToRemoveObjIds)) {
						it.remove();
						found = true;
						break;
					}
				} else {
					if(elmt.getAsString().equals(value)) {
						it.remove();
						found = true;
						break;
					}
				}
			}
			
			// write the file
			if(found) {
				this.writeConfigurationFile(speechFile, root);
			} else {
				throw new GrammarConfigurationParseException("The value '" + value + "' does not exist in values for query '" + entry + "'");
			}
		} catch (FileNotFoundException e) {
			throw new GrammarConfigurationParseException("Speech file not found", e);
		}
	}
	
	/**
	 * Parse the speech configuration file and find the node targeted be the query
	 * @param entryQuery Query
	 * @return Found node
	 * @throws GrammarConfigurationParseException If the query is wrong or the node can't be found
	 */
	private JsonArray findNode(JsonElement root, String entryQuery) throws GrammarConfigurationParseException {
		JsonObject element = root.getAsJsonObject();
		
		// for each part of the entry query
		Iterator<String> treeEntries = Arrays.asList(entryQuery.split("\\.")).iterator();
		while(treeEntries.hasNext()) {
			String entry = treeEntries.next();
			Matcher m = treeEntryPattern.matcher(entry);
			if(m.matches()) {
				String entryName = m.group(1);
				
				// if there is some filter
				String filterPropertyName = m.groupCount() > 2 ? m.group(3) : null;
				String filterPropertyValue = m.groupCount() > 2 ? m.group(4) : null;
				
				// if it's the last property
				boolean isLast = !treeEntries.hasNext();
			
				// if last property then the value should be an array of values that can be edited
				if(isLast) {
					// should be an array of values to edit
					if(element.get(entryName).isJsonArray()) {
						return element.getAsJsonArray(entryName);
					}
					throw new GrammarConfigurationParseException("The final value of query '" + entryQuery + "'is not an array that can be edited");
				} else {
					// if there is a filter 
					if(filterPropertyName != null && filterPropertyValue != null) {
						// if should be an array in order to be filtered
						if(element.get(entryName).isJsonArray()) {
							JsonArray array = element.getAsJsonArray(entryName);
							Iterator<JsonElement> it = array.iterator();
							boolean filteredEntryFound = false;
							while(it.hasNext()) {
								JsonElement elmt = it.next();
								
								// if the element of the array is an object, try to get the filterPropertyName and match it with the value that has been specified
								if(elmt.isJsonObject()) {
									JsonObject o = elmt.getAsJsonObject();
									if(o.has(filterPropertyName)) {
										String filterValue = o.get(filterPropertyName).getAsString();
										if(filterValue != null && filterValue.equals(filterPropertyValue)) {
											// the element has been found, leave the loop and move to the next entry
											element = o;
											filteredEntryFound = true;
											break;
										}
									}
								}
							}
							
							// move to the next entry
							if(filteredEntryFound) {
								continue;
							}
							throw new GrammarConfigurationParseException("No entry found for entry '"+ entry +"'");
						}
						throw new GrammarConfigurationParseException("The query '"+ entryQuery +"' can't be parsed - entry '" + entryName + "' should be an array");
					} 
					// no filter, get the next element and process the next entry name
					else {
						element = element.getAsJsonObject(entryName);
					}
				}
			} else {
				throw new GrammarConfigurationParseException("The entry '" + entryQuery + "' can't be parsed");
			}
		}
		throw new GrammarConfigurationParseException("the query '" + entryQuery + "' can't be parsed");
	}
	
	/**
	 * 
	 * @param elmt
	 * @return
	 */
	private Map<String, String> getPrimitiveProperties(JsonObject elmt){
		return elmt.getAsJsonObject().entrySet()
				.stream()
				.filter(e -> e.getValue().isJsonPrimitive())
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getAsString()));
	}
	
	/**
	 * 
	 * @param file
	 * @param root
	 * @throws GrammarConfigurationParseException
	 */
	private void writeConfigurationFile(File file, JsonElement root) throws GrammarConfigurationParseException {
		try (Writer writer = new FileWriter(file)) {
			new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(root, writer);
		} catch (IOException e) {
			throw new GrammarConfigurationParseException("Can't write configuration file", e);
		}
	}
}
