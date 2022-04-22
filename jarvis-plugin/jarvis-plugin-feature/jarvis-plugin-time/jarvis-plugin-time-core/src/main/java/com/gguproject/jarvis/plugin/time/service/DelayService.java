package com.gguproject.jarvis.plugin.time.service;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.listener.AsbtractSpeechCommandProcessor;
import com.gguproject.jarvis.plugin.speech.listener.SpeechEventListener;

import javax.inject.Named;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Named
public class DelayService {

    private static Timer timer = new Timer();

    public void delay(Runnable runnable, Date scheduledDate){
        timer.schedule(new CommandTimerTask(runnable), scheduledDate);
    }

    /**
     * Task that will be executed at the given time
     * Used for delayed commands
     */
    public static class CommandTimerTask extends TimerTask {
        private static final Logger LOGGER = AbstractLoggerFactory.getLogger(CommandTimerTask.class);

        private final Runnable runnable;

        public CommandTimerTask(Runnable runnable) {
            this.runnable = runnable;
        }

        public void run() {
            Thread.currentThread().setName("TIMER");
            LOGGER.debug("Start scheduled timer task");
            this.runnable.run();
        }
    }
}
