package com.gguproject.jarvis.plugin.speech.interpreter.grammar;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.gguproject.jarvis.plugin.speech.sound.SoundMatch;

public class GrammarWord {

	private String fullWord;
	
	private List<String> words;
	
	private List<String> encodedWords;
	
	private String encodedWord;
	
	public static GrammarWord build(String word) {
		return new GrammarWord(word);
	}
	
	private GrammarWord(String word) {
		this.fullWord = word;
		this.words = Arrays.asList(this.fullWord.split(" "));
		this.encodedWords = this.words.stream().map(w -> SoundMatch.encode(w)).collect(Collectors.toList());
		this.encodedWord = SoundMatch.encode(this.fullWord);
	}
	
	public boolean match(String word, int length) {
		String encodedSubstringActionWord = StringUtils.join(this.encodedWords.subList(0, length), " ");
		String substringActionWord = StringUtils.join(this.words.subList(0,  length), " ");
		String encodedTestValue = SoundMatch.encode(word);
		int distance = SoundMatch.distance(encodedSubstringActionWord, encodedTestValue);
		int threshold = SoundMatch.calculateThreshold(word, substringActionWord);
		return distance <= threshold;
	}
	
	public boolean match(String word) {
		int distance = distance(word);
		int threshold = SoundMatch.calculateThreshold(word, this.fullWord);
		return distance <= threshold;
	}
	
	public boolean exactMatch(String word) {
		return this.fullWord.equals(word);
	}
	
	public boolean startsWith(String word) {
		return this.fullWord.startsWith(word);
	}
	
	public int distance(String word) {
		String encodedTestValue = SoundMatch.encode(word);
		return SoundMatch.distance(this.encodedWord, encodedTestValue);
	}

	public String getFullWord() {
		return fullWord;
	}
	
	public int getWordLength() {
		return this.words.size();
	}
	
	public String getWord(int index) {
		return this.words.get(index);
	}

	public String getEncodedWord() {
		return encodedWord;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.fullWord == null) ? 0 : this.fullWord.hashCode());
		return result;
	}
	
	public boolean equals(String word) {
		return this.fullWord.equals(word);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GrammarWord other = (GrammarWord) obj;
		if (this.fullWord == null) {
			if (other.fullWord != null)
				return false;
		} else if (!this.fullWord.equals(other.fullWord))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GrammarWord [fullWord=" + fullWord + ", encodedWord=" + encodedWord + "]";
	}
}
