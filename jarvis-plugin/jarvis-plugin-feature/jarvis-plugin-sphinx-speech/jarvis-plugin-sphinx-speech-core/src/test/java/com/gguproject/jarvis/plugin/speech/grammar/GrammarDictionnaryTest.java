package com.gguproject.jarvis.plugin.speech.grammar;

import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.speech.sphinx.SphinxDictionnaryService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class GrammarDictionnaryTest {

	private SphinxDictionnaryService service;
	
	private File testDictionnaryFile;
	
	@BeforeEach
	public void initTest() throws IllegalArgumentException, IllegalAccessException, IOException {
		// create a temporary file which will be deleted when leaving test
		File dictionnaryFile = new File(this.getClass().getClassLoader().getResource("jarvis.dictionnary.dic").getFile());
		this.testDictionnaryFile = File.createTempFile(UUID.randomUUID().toString(), "json");
		this.testDictionnaryFile.deleteOnExit();
		FileUtils.copyFile(dictionnaryFile, this.testDictionnaryFile);
				
		// mock configuration class
		SpeechPluginConfiguration configuration = Mockito.mock(SpeechPluginConfiguration.class);
		Mockito.reset(configuration);
		Mockito.when(configuration.getProperty(PropertyKey.sphinxFullDictionaryPath)).thenReturn("fulldictionnarypath.dic");
		File fullDictionnaryFile = new File(this.getClass().getClassLoader().getResource("frenchWords62K.dic").getFile());
		Mockito.when(configuration.getConfigurationFile("fulldictionnarypath.dic")).thenReturn(Optional.of(fullDictionnaryFile));
		Mockito.when(configuration.getConfigurationFileOrCreate("fulldictionnarypath.dic")).thenReturn(fullDictionnaryFile);

		Mockito.when(configuration.getProperty(PropertyKey.sphinxDictionaryPath)).thenReturn("jarvis.dictionnary.dic");
		Mockito.when(configuration.getConfigurationFile("jarvis.dictionnary.dic")).thenReturn(Optional.of(this.testDictionnaryFile));
		Mockito.when(configuration.getConfigurationFileOrCreate("jarvis.dictionnary.dic")).thenReturn(this.testDictionnaryFile);
		
		// build the service to test
		this.service = new SphinxDictionnaryService(configuration);
	}
}
