package com.gguproject.jarvis.plugin.deepspeech.service;

import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.daemon.DaemonThread;
import com.gguproject.jarvis.core.ioc.context.annotation.Prototype;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.deepspeech.DeepspeechPluginConfiguration;
import com.gguproject.jarvis.plugin.deepspeech.DeepspeechPluginConfiguration.PropertyKey;
import com.gguproject.jarvis.plugin.deepspeech.sound.service.SoundCleaningService;
import com.gguproject.jarvis.plugin.speech.event.SpeechEventData;
import com.gguproject.jarvis.plugin.speech.interpreter.grammar.GrammarWord;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;

/**
 * Thread which starts the deepspeech process and broadcast a command if found
 */
@Named
@Prototype
public class DeepspeechThread extends DaemonThread implements OnPostConstruct {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(DeepspeechThread.class);

    private final DeepspeechService deepspeechService;

    private final DeepspeechPluginConfiguration configuration;

    private final SoundCleaningService soundMatchingService;

    private final EventBusService eventBusService;

    private GrammarWord keyword;

    public DeepspeechThread(DeepspeechService deepspeechService, DeepspeechPluginConfiguration configuration,
                            SoundCleaningService soundMatchingService, EventBusService eventBusService) {
        super("DEEPSPEECH_MANAGER");
        this.deepspeechService = deepspeechService;
        this.configuration = configuration;
        this.soundMatchingService = soundMatchingService;
        this.eventBusService = eventBusService;
    }

    @Override
    public void postConstruct() {
        this.keyword = GrammarWord.build(configuration.getProperty(PropertyKey.keyword));
    }

    @Override
    public void run() {
        LOGGER.info("Ready to to analyse speech " + Thread.currentThread().getId());

        this.deepspeechService.cleanupExistingProcesses();
        this.deepspeechService.startProcess(speech -> {
            // check if the first word matchs the keyword (Jarvis)
            String firstWord = StringUtils.substringBefore(speech, " ");
            if (this.keyword.match(firstWord)) {
                LOGGER.debug("Found speech: {}", speech);
                // clean the speech by removing small words and the first word (which should be the keyword)
                String cleanedSpeech = this.soundMatchingService.clean(StringUtils.substringAfter(speech, " "));
                // then broadcast it
                if (StringUtils.isNotEmpty(cleanedSpeech)) {
                    this.eventBusService.externalEmit(new SpeechEventData(cleanedSpeech));
                }
            }
        });
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.deepspeechService.interrupt();
    }
}
