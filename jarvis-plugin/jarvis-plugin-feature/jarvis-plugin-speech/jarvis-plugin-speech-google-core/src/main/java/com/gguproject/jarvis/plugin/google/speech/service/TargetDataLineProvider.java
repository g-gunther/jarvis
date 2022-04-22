package com.gguproject.jarvis.plugin.google.speech.service;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import javax.inject.Named;
import javax.sound.sampled.*;

public class TargetDataLineProvider {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(TargetDataLineProvider.class);

    private static final TargetDataLineProvider instance = new TargetDataLineProvider();

    private TargetDataLine targetDataLine;

    private AudioInputStream audioInputStream;

    public TargetDataLineProvider(){
        try {
            AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
            this.targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
            this.targetDataLine.open();
            this.audioInputStream = new AudioInputStream(this.targetDataLine);
        } catch (LineUnavailableException e) {
            LOGGER.error("Not able to initialize the target data line", e);
        }
    }

    public static TargetDataLine getTargetDataLine(){
        return instance.targetDataLine;
    }

    public static void start(){
        instance.getTargetDataLine().start();
    }

    public static void stop(){
        instance.getTargetDataLine().stop();
    }

    public static AudioInputStream getStream(){
        return instance.audioInputStream;
    }

}
