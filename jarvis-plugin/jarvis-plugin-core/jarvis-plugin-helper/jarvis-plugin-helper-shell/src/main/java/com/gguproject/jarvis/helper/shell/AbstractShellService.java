package com.gguproject.jarvis.helper.shell;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import java.io.IOException;

public class AbstractShellService {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(AbstractShellService.class);
	
	/** Default sleep period */
	private final static String DEFAULT_SLEEP_DURATION = "0.05";
	
	/**
	 * Process a single command
	 * @param command Command to process
	 * @throws ShellCommandException
	 */
	protected void exec(String command) throws ShellCommandException{
		LOGGER.debug("Process command: {}", command);
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
	 * @param commands Commands
	 * @throws ShellCommandException 
	 */
	protected void exec(String[] commands) throws ShellCommandException{
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
	 * @throws ShellCommandException
	 */
	protected void sleep(String duration) throws ShellCommandException{
		this.exec("sleep " + duration);
	}
	
	/**
	 * Sleep for the default value
	 * @throws ShellCommandException
	 */
	protected void sleep() throws ShellCommandException {
		this.sleep(this.getSleepDuration());
	}
	
	/**
	 * Return the sleep duration - can be overriden
	 * @return
	 */
	protected String getSleepDuration() {
		return DEFAULT_SLEEP_DURATION;
	}
}
