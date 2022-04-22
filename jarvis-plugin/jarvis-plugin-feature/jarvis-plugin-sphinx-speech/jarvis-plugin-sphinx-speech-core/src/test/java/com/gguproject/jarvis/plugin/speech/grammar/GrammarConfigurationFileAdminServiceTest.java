package com.gguproject.jarvis.plugin.speech.grammar;

import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.speech.grammar.dto.Grammar;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
public class GrammarConfigurationFileAdminServiceTest {

	private GrammarConfigurationFileAdminService service;
	private GrammarConfigurationFileService speechConfigurationService;
	
	private File speechFile;
	
	public GrammarConfigurationFileAdminServiceTest() {
		this.speechFile = new File(this.getClass().getClassLoader().getResource("speech.json").getFile());
	}
	
	@BeforeEach
	public void initTest() throws IllegalArgumentException, IllegalAccessException, IOException {
		// create a temporary file which will be deleted when leaving test
		File testSpeechFile = File.createTempFile(UUID.randomUUID().toString(), "json");
		testSpeechFile.deleteOnExit();
		
		FileUtils.copyFile(this.speechFile, testSpeechFile);
		
		// mock configuration class
		SpeechPluginConfiguration configuration = Mockito.mock(SpeechPluginConfiguration.class);
		Mockito.reset(configuration);
		Mockito.when(configuration.getProperty(PropertyKey.configuration)).thenReturn("speech.json");
		Mockito.when(configuration.getConfigurationFile("speech.json")).thenReturn(Optional.of(testSpeechFile));
		Mockito.when(configuration.getConfigurationFileOrCreate("speech.json")).thenReturn(testSpeechFile);
		
		// build the service to test
		this.service = new GrammarConfigurationFileAdminService(configuration);
		
		// service which will be used to test 
		this.speechConfigurationService = new GrammarConfigurationFileService(configuration);
	}
	
	@Test
	public void test() {
		Grammar grammar = speechConfigurationService.parseConfigurationFile();
		speechConfigurationService.writeConfigurationFile(grammar);
	}
	
	@Test
	public void testAddEntry() throws GrammarConfigurationParseException {
		this.service.addEntry("context[type=TV].actions", "test");
		
		Grammar grammar = speechConfigurationService.parseConfigurationFile();
		
		long count = grammar.getContext().stream().filter(e -> e.getType().equals("TV") && e.containsAction("test")).count();
		
		assertEquals(1, count);
	}
	
	@Test
	public void testAddEntryalreadyExists() {
		assertThrows(GrammarConfigurationParseException.class, () -> {
			this.service.addEntry("context[type=TV].actions", "{\"action\":\"SET\"}");
		});
	}
	
	@Test
	public void testRemoveEntry() throws GrammarConfigurationParseException {
		Grammar grammar = speechConfigurationService.parseConfigurationFile();
		long count = grammar.getContext().stream().filter(e -> e.getType().equals("TV") && e.containsAction("UNMUTE")).count();
		assertEquals(1, count);
		
		this.service.removeEntry("context[type=TV].actions", "UNMUTE");
		
		grammar = speechConfigurationService.parseConfigurationFile();
		count = grammar.getContext().stream().filter(e -> e.getType().equals("TV") && e.containsAction("UNMUTE")).count();
		assertEquals(0, count);
	}
	
	@Test
	public void testRemoveEntryDontExists() throws GrammarConfigurationParseException {
		Grammar grammar = speechConfigurationService.parseConfigurationFile();
		long count = grammar.getContext().stream().filter(e -> e.getType().equals("TV") && e.containsAction("test")).count();
		assertEquals(0, count);
		
		assertThrows(GrammarConfigurationParseException.class, () -> {
			this.service.removeEntry("context[type=TV].actions", "test");
		});
	}
}
