package com.gguproject.jarvis.master;

import com.gguproject.jarvis.core.events.api.multicast.MulticastManager;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.command.event.ExternalCommandRequestEventData;

import javax.inject.Named;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Named
public class SystemCommandReceiver {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(SystemCommandReceiver.class);

    private BufferedReader inputReader;

    private final MulticastManager multicastManager;

    public SystemCommandReceiver(MulticastManager multicastManager) {
        this.multicastManager = multicastManager;

        InputStreamReader in = new InputStreamReader(System.in);
        inputReader = new BufferedReader(in);
    }

    public void start() {
        try {
            String command;
            while ((command = this.inputReader.readLine()) != null) {
                LOGGER.info("Send command: {}", command);
                multicastManager.publish(new ExternalCommandRequestEventData(command));
            }
        } catch (IOException e) {
            LOGGER.error("Error while listning to the stsme input stream", e);
        }
    }
}
