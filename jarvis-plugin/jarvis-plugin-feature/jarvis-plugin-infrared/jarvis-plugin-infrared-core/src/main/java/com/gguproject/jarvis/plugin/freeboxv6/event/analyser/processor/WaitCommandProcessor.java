package com.gguproject.jarvis.plugin.freeboxv6.event.analyser.processor;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

/**
 * It waits before processing the commands
 */
public class WaitCommandProcessor implements CommandProcessor {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(WaitCommandProcessor.class);

    private int duration;

    public WaitCommandProcessor(int duration) {
        this.duration = duration;
    }

    @Override
    public void process() {
        LOGGER.debug("Wait for {}", this.duration);
        try {
            Thread.sleep(this.duration);
        } catch (InterruptedException e) {
            LOGGER.error("Thread sleep error", e);
        }
    }

    @Override
    public String toString() {
        return "WaitCommandProcessor [duration=" + duration + "]";
    }
}