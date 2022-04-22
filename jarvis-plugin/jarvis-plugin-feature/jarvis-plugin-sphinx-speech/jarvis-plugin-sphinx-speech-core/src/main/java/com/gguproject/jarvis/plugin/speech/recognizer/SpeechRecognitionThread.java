package com.gguproject.jarvis.plugin.speech.recognizer;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.daemon.DaemonThread;
import com.gguproject.jarvis.core.ioc.context.annotation.Prototype;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterException;
import com.gguproject.jarvis.plugin.speech.interpreter.InterpreterService;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;

import javax.inject.Named;

/**
 * Thread which listen to the microphone to recognize some words based on the defined grammar
 * and tries to interpret it to retrieve a command to broadcast
 */
@Named
@Prototype
public class SpeechRecognitionThread extends DaemonThread {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SpeechRecognitionThread.class);

    private final SpeechRecognition speechRegognition;

    private final EventBusService eventBusService;

    private final InterpreterService interpreterService;

    public SpeechRecognitionThread(SpeechRecognition speechRegognition, EventBusService eventBusService,
        InterpreterService interpreterService) {
        super("SPEECH_MANAGER");
        this.speechRegognition = speechRegognition;
        this.eventBusService = eventBusService;
        this.interpreterService = interpreterService;
    }

    @Override
    public void run() {
        LOGGER.info("Ready to to analyse speech " + Thread.currentThread().getId());

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
        String speech = this.speechRegognition.waitForNextSpeech();

        if (this.interpreterService.canProcess(speech)) {
            LOGGER.debug("Try to interpret: {}", speech);
            try {
                Command command = this.interpreterService.interprets(speech);
                LOGGER.debug("Found command: {}", command);

                //this.eventBusService.externalEmit(new InfraredCommandEventData(command.getContext(), command.getAction(), command.getData()))));
            } catch (InterpreterException e) {
                LOGGER.error("Can't analyze speech {} - fails with error", speech, e);
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
