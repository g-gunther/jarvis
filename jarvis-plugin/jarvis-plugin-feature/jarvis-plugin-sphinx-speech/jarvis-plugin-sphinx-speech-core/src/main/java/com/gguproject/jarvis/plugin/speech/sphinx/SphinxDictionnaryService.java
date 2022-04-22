package com.gguproject.jarvis.plugin.speech.sphinx;

import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration;
import com.gguproject.jarvis.plugin.speech.SpeechPluginConfiguration.PropertyKey;

import javax.inject.Named;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This service is used to update the speech recognition dictionary
 * by adding or removing words
 */
@Named
public class SphinxDictionnaryService {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SphinxDictionnaryService.class);

	private final SpeechPluginConfiguration configuration;

	public SphinxDictionnaryService(SpeechPluginConfiguration configuration){
		this.configuration = configuration;
	}

	public Collection<String> parseSentenceToWords(String sentence){
		return Arrays.asList(sentence.replace("-", " ").split(" "));
	}
	
	/**
	 * Split a sentence into several words and add them to the dictionnary 
	 * @param sentence
	 * @throws SphinxDictionnaryException
	 */
	public void addWordsInDictionnary(String sentence) throws SphinxDictionnaryException {
		this.addWordsInDictionnary(this.parseSentenceToWords(sentence));
	}
	
	/**
	 * Add a list of words to the dictionnary
	 * @param words Words to add
	 * @throws SphinxDictionnaryException
	 */
	public void addWordsInDictionnary(Collection<String> words) throws SphinxDictionnaryException {
		List<String> lowercaseWords = words.stream().map(String::toLowerCase).collect(Collectors.toList());
		List<String> phonemes = new ArrayList<>();
		for(String word : lowercaseWords) {
			if(this.isWordInDictionnary(word)) {
				LOGGER.info("The word {} has already been added", word);
				continue;
			}
			
			phonemes.addAll(this.findPhonemesForWord(word));
			if(phonemes.isEmpty()) {
				throw new SphinxDictionnaryException("Can't find phonemes for word: " + word);
			}
		}
		
		this.addPhonemesInDictionnary(phonemes);
	}
	
	/**
	 * Remove a list of words of the dictionnary
	 * @param words Words to remove
	 * @throws SphinxDictionnaryException
	 */
	public void removeWordsFromDictionnary(List<String> words) throws SphinxDictionnaryException {
		List<String> lowercaseWords = words.stream().map(String::toLowerCase).collect(Collectors.toList());
		File dictionnaryFile = this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.sphinxDictionaryPath))
				.orElseThrow(() -> TechnicalException.get().message("not able to remove word {0} from dictionary because the file does not exist", words).build());

		File tempFile;
		try {
			tempFile = File.createTempFile("dictionnary", ".tmp");
		} catch (IOException e) {
			throw new SphinxDictionnaryException("Can't create temporary file", e);
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(dictionnaryFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))){

			String line;
			mainLoop: while((line = reader.readLine()) != null) {
				for(String w : lowercaseWords) {
					if(this.checkLineWithWord(line, w)) {
						continue mainLoop;
					}
				}
				
			    writer.write(line);
			    writer.newLine();
			}
		} catch (IOException e) {
			throw new SphinxDictionnaryException("Can't add words to dictionnary file", e);
		}
		
		if(!tempFile.renameTo(dictionnaryFile)) {
			throw new SphinxDictionnaryException("Can't rename temporary dictionnary file");
		}
	}
	
	/**
	 * Parse the sphinx dictionnary used by the application and check if
	 * the given word has already been added or not
	 * @param word Word to check
	 * @return True is added, false else
	 * @throws SphinxDictionnaryException
	 */
	private boolean isWordInDictionnary(String word) throws SphinxDictionnaryException {
		File dictionnaryFile = this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.sphinxDictionaryPath))
				.orElseThrow(() -> TechnicalException.get().message("Not able to check if word {0} is in dictionary because the file does not exist", word).build());
		
		try(FileReader fr = new FileReader(dictionnaryFile); BufferedReader br = new BufferedReader(fr)) {
			String line;
			while ((line = br.readLine()) != null) {
				if(this.checkLineWithWord(line, word)) {
					return true;
				}
			}
		} catch (IOException e) {
			throw new SphinxDictionnaryException("Can't parse the sphinx dictionnary file", e);
		}
		
		return false;
	}
	
	/**
	 * Find all phonemes for a given words
	 * @param word Word to search
	 * @return list of phonemes by words
	 * @throws SphinxDictionnaryException
	 */
	private List<String> findPhonemesForWord(String word) throws SphinxDictionnaryException{
		List<String> phonemes = new ArrayList<>();
		File fullDictionnaryFile = this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.sphinxFullDictionaryPath))
				.orElseThrow(() -> TechnicalException.get().message("Not able to find phoenems for word {0} because file does not exist", word).build());

		try(FileReader fr = new FileReader(fullDictionnaryFile); BufferedReader br = new BufferedReader(fr)) {
			String line;
			while ((line = br.readLine()) != null) {
				if(this.checkLineWithWord(line, word)) {
					phonemes.add(line);
				}
			}
		} catch (IOException e) {
			throw new SphinxDictionnaryException("Can't parse the sphinx full dictionnary file", e);
		}
		
		return phonemes;
	}
	
	/**
	 * Check if a line of the dictionnary is related to the given word
	 * @param line
	 * @param word
	 * @return
	 */
	private boolean checkLineWithWord(String line, String word) {
		if(line.startsWith(word)) {
			String wordLine = line.split(" ")[0];
			int bracketIndex = wordLine.indexOf("(");
			if(bracketIndex > 0) {
				return wordLine.substring(0, bracketIndex).equals(word);
			} else {
				return wordLine.equals(word);
			}
		}
		return false;
	}
	
	/**
	 * Add new word in the dictionnary
	 * @param words Words to add
	 * @throws SphinxDictionnaryException
	 */
	private void addPhonemesInDictionnary(List<String> words) throws SphinxDictionnaryException {
		File dictionnaryFile = this.configuration.getConfigurationFile(this.configuration.getProperty(PropertyKey.sphinxDictionaryPath))
				.orElseThrow(() -> TechnicalException.get().message("not able to add phonemes for words: {0} because file does not exist", words).build());

		try (FileWriter fr = new FileWriter(dictionnaryFile, true); BufferedWriter bw = new BufferedWriter(fr)){
			for(String word : words) {
				bw.newLine();
				bw.write(word);
			}
		} catch(IOException e) {
			throw new SphinxDictionnaryException("Can't add words to dictionnary file", e);
		}
	}
}
