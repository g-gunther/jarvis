package com.gguproject.jarvis.plugin.androidtv.listener;

import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.androidtv.encoder.Key.Code;
import com.gguproject.jarvis.plugin.androidtv.service.AndroidTvRemoteControlService;
import com.gguproject.jarvis.plugin.androidtv.util.AndroidTvException;
import com.gguproject.jarvis.plugin.speech.interpreter.support.Command;
import com.gguproject.jarvis.plugin.speech.listener.AsbtractSpeechCommandProcessor;

//@Named
public class AndroidTvStart extends AsbtractSpeechCommandProcessor {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(AndroidTvStart.class);

	private final AndroidTvRemoteControlService androidTvRemoteControlService;

	public AndroidTvStart(AndroidTvRemoteControlService androidTvRemoteControlService) {
		super("TV", "START");
		this.androidTvRemoteControlService = androidTvRemoteControlService;
	}

	@Override
	public void process(DistributedEvent event, Command data) {
		LOGGER.info("Try to start the android tv");
		try {
			this.androidTvRemoteControlService.send(Code.KEYCODE_POWER);
			Thread.sleep(15000); // wait until the box is started
			this.androidTvRemoteControlService.send(Code.KEYCODE_HOME);
			Thread.sleep(500);
			this.androidTvRemoteControlService.send(Code.KEYCODE_DPAD_DOWN);
			Thread.sleep(500);
			this.androidTvRemoteControlService.send(Code.KEYCODE_ENTER);
		} catch (AndroidTvException | InterruptedException e) {
			LOGGER.error("An error occurs while changing the tv channel", e);
		}
	}
}