package com.gguproject.jarvis.helper.sound;

import java.io.File;
import java.io.IOException;

import javax.inject.Named;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

@Named
public class SoundPlayerService {

	private final Clip clip;
	
	public SoundPlayerService() {
		try {
			this.clip = AudioSystem.getClip();
		} catch (LineUnavailableException e) {
			throw new IllegalStateException("Not able to set up sound player service", e);
		}
	}
	
	public void playOnce(File soundFile) throws SoundPlayerException {
		this.stop();
		
		try {
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(soundFile);
			this.clip.open(inputStream);
			this.clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			throw new SoundPlayerException("An error occurs while playing sound: " + soundFile, e);
		}
	}
	
	public void playLoop(File soundFile) throws SoundPlayerException {
		this.stop();
		
		try {
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(soundFile);
			this.clip.open(inputStream);
	        this.clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			throw new SoundPlayerException("An error occurs while playing sound: " + soundFile, e);
		}
	}
	
	public void stop() {
		if(this.clip.isActive() || this.clip.isOpen()) {
			this.clip.stop();
			this.clip.close();
		}
	}
}
