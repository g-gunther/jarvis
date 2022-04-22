package com.gguproject.jarvis.plugin.google.speech;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.daemon.DaemonThread;
import com.gguproject.jarvis.core.ioc.context.annotation.Prototype;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.helper.google.counter.GoogleCounterException;
import com.gguproject.jarvis.helper.light.LedService;
import com.gguproject.jarvis.plugin.google.speech.SpeechPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.google.speech.service.GoogleSpeechCounterService;
import com.gguproject.jarvis.plugin.google.speech.service.GoogleSpeechCounterService.CounterName;
import com.gguproject.jarvis.plugin.google.speech.service.GoogleSpeechService;
import com.gguproject.jarvis.plugin.google.speech.service.GoogleSpeechService.GoogleSpeechServiceBuilder;
import com.gguproject.jarvis.plugin.speech.event.SpeechEventData;
import com.gguproject.jarvis.plugin.sphinx.speech.SpeechRecognition;

import javax.inject.Named;

/**
 * Thread which listen to the microphone to recognize some words based on the defined grammar
 * and tries to interpret it to retrieve a command to broadcast
 */
@Named
@Prototype
public class SpeechRecognitionThread extends DaemonThread {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpeechRecognitionThread.class);

    private final SpeechPluginConfiguration configuration;

    private final LedService ledService;

    private final SpeechRecognition speechRegognition;

    private final GoogleSpeechCounterService googleCounterService;

    private final EventBusService eventBusService;

    private GoogleSpeechService googleSpeechService;

    public SpeechRecognitionThread(SpeechPluginConfiguration configuration, LedService ledService, SpeechRecognition speechRegognition,
                                   GoogleSpeechCounterService googleCounterService, EventBusService eventBusService) {
        super("SPEECH_MANAGER");
        this.configuration = configuration;
        this.ledService = ledService;
        this.speechRegognition = speechRegognition;
        this.googleCounterService = googleCounterService;
        this.eventBusService = eventBusService;
    }

    @Override
    public void run() {
        LOGGER.info("Ready to to analyse speech");

        this.googleSpeechService = GoogleSpeechServiceBuilder.get()
                .credentialFilePath(this.configuration.getSecretDataFilePath(this.configuration.getProperty(PropertyKey.googleCredentialPath)))
                .listeningTime(this.configuration.getIntProperty(PropertyKey.googleListeningTime))
                .language(this.configuration.getProperty(PropertyKey.googleLanguage))
                .onSpeech(speech -> {
                    LOGGER.debug("Google speech: {}", speech);
                    this.eventBusService.externalEmit(new SpeechEventData(speech));
                })
                .onStart(() -> this.ledService.turnOn())
                .onComplete(() -> this.ledService.turnOff())
                .build();

        while (true) {
            try {
                run0();
            } catch (InterruptedException e) {
                LOGGER.error("Stop the speech recognition thread (interrupted)", e);
                return;
            }
        }
    }

    private void run0() throws InterruptedException {
        // wait for the next Jarvis word
        this.speechRegognition.waitForNextSpeech();

        if (this.configuration.getBoolProperty(PropertyKey.googleDisabled)) {
            LOGGER.info("Google speech has been disabled - skip");
        } else {
            try {
                this.googleCounterService.check(CounterName.SPEECH_REQUEST);
            } catch (GoogleCounterException e) {
                LOGGER.warn("Can't call google-speech service", e);
                return;
            }

            this.googleSpeechService.listen();

            try {
                this.googleCounterService.increment(CounterName.SPEECH_REQUEST);
            } catch (GoogleCounterException e) {
                LOGGER.error("An error occurs while incrementing SPEECH_REQUEST counter", e);
            }
        }

        // Interrupted?
        if (isInterrupted()) {
            throw new InterruptedException();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.speechRegognition.interrupt();
    }
}
