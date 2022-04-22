package com.gguproject.jarvis.plugin.speech.interpreter;

import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.speech.grammar.GrammarConfigurationFileService;
import com.gguproject.jarvis.plugin.speech.grammar.GrammarService;
import com.gguproject.jarvis.plugin.speech.grammar.dto.Grammar;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterServiceTest {

	private InterpreterService service;
	
	public InterpreterServiceTest() throws IllegalArgumentException, IllegalAccessException {
		// get the speech description file from test resources
		File speechFile = new File(this.getClass().getClassLoader().getResource("speech.json").getFile());

		// mock configuration class
		SpeechPluginConfiguration configuration = Mockito.mock(SpeechPluginConfiguration.class);
		Mockito.when(configuration.getProperty(PropertyKey.configuration)).thenReturn("speech.json");
		Mockito.when(configuration.getConfigurationFile("speech.json")).thenReturn(Optional.of(speechFile));
		
		// process the grammar file using the file processor
		GrammarConfigurationFileService speechConfigurationService = new GrammarConfigurationFileService(configuration);
		Grammar grammar = speechConfigurationService.parseConfigurationFile();
		
		// mock grammar service
		GrammarService grammarService = Mockito.mock(GrammarService.class);
		Mockito.when(grammarService.getGrammar()).thenReturn(grammar);
		
		// build the service to test
		this.service = new InterpreterService(grammarService);
	}
	
	@Test
	public void testStartTv() throws InterpreterException {
		this.doTest("jarvis allume la tv", "START", "TV");
	}
	
	@Test
	public void testSetTvChannel() throws InterpreterException {
		this.doTest("jarvis mets la deux", "SET", "TV", "deux");
		this.doTest("jarvis mets tf1", "SET", "TV", "tf1");
	}
	
	@Test
	public void testShudown() throws InterpreterException {
		this.doTest("jarvis éteint toi", "STOP", "SELF");
		this.doTest("jarvis arrête toi", "STOP", "SELF");
	}
	
	@Test
	public void testMusicPlaylist() throws InterpreterException {
		this.doTest("jarvis mets fourre tout dans la cuisine", "SET", "MUSIC", "fourre tout", "KITCHEN");
		this.doTest("jarvis mets la playlist fourre tout", "SET", "MUSIC", "fourre tout");
		this.doTest("jarvis mets fourre tout", "SET", "MUSIC", "fourre tout");
		this.doTest("jarvis mets la musique", "SET", "MUSIC");
		this.doTest("jarvis allume la musique", "START", "MUSIC", null);
	}
	
	@Test
	public void testLightInRoom() throws InterpreterException {
		this.doTest("jarvis mets les lumière dans la chambre", "SET", "LIGHT", null, "ROOM");
	}
	
	@Test
	public void testMuteTv() throws InterpreterException {
		this.doTest("jarvis coupe le son de la tv", "MUTE", "TV");
		this.doTest("jarvis remets le son de la tv", "UNMUTE", "TV");
	}
	
	@Test
	public void testTvSound() throws InterpreterException {
		this.doTest("jarvis allume le son de la tv", "START", "MUSIC", "tv");
		this.doTest("jarvis mets le son de la tv", "SET", "MUSIC", "tv");
	}
	
	@Test
	public void testTvStart() throws InterpreterException {
		this.doTest("jarvis allume la télé", "START", "TV");
	}
	
	private void doTest(String speech, String assertAction, String assertContext) throws InterpreterException {
		this.doTest(speech, assertAction, assertContext, null);
	}
	
	private void doTest(String speech, String assertAction, String assertContext, String assertData) throws InterpreterException {
		this.doTest(speech, assertAction, assertContext, assertData, null);
	}
	
	private void doTest(String speech, String assertAction, String assertContext, String assertData, String assertLocalization) throws InterpreterException {
		Command c = this.service.interprets(speech);
		assertEquals(c.getAction(), assertAction);
		assertEquals(c.getContext(), assertContext);
		assertEquals(c.getData(), assertData);
		assertEquals(c.getLocalisation(), assertLocalization);
	}
}
