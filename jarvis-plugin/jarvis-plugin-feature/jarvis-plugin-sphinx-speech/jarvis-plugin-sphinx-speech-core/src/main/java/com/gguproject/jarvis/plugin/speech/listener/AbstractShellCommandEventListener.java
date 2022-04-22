package com.gguproject.jarvis.plugin.speech.listener;

import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import java.io.IOException;

/**
 * Shell command listener
 * Used to process some {@link InfraredCommandEventData} with shell command
 *
 * @author guillaumegunther
 */
public abstract class AbstractShellCommandEventListener extends AbstractCommandEventListener {
    private static final Logger LOG = AbstractLoggerFactory.getLogger(AbstractShellCommandEventListener.class);

    /**
     * Default sleep period
     */
    private final static String DEFAULT_SLEEP_DURATION = "0.05";

    public AbstractShellCommandEventListener(EventBusService eventBusService, String contextType, String... actionTypes) {
        super(eventBusService, contextType, actionTypes);
    }

    /**
     * Process a single command
     *
     * @param command Command to process
     * @throws ShellCommandException
     */
    protected void exec(String command) throws ShellCommandException {
        LOG.debug("Process command: {}", command);
        try {
            Runtime.getRuntime().exec(command).waitFor();
        } catch (IOException e) {
            throw new ShellCommandException(e);
        } catch (InterruptedException e) {
            throw new ShellCommandException(e);
        }
    }

    /**
     * Process a list of commands
     *
     * @param commands Commands
     * @throws ShellCommandException
     */
    protected void exec(String[] commands) throws ShellCommandException {
        try {
            Runtime.getRuntime().exec(commands).waitFor();
        } catch (IOException e) {
            throw new ShellCommandException(e);
        } catch (InterruptedException e) {
            throw new ShellCommandException(e);
        }
    }

    /**
     * Sleep for few milliseconds
     *
     * @throws ShellCommandException
     */
    protected void sleep(String duration) throws ShellCommandException {
        this.exec("sleep " + duration);
    }

    /**
     * Sleep for the default value
     *
     * @throws ShellCommandException
     */
    protected void sleep() throws ShellCommandException {
        this.sleep(DEFAULT_SLEEP_DURATION);
    }
}
