package com.gguproject.jarvis.plugin.androidtv.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.androidtv.client.PinHandler;
import com.gguproject.jarvis.plugin.androidtv.event.AndroidTvPinEventData;

import javax.inject.Named;

/**
 * Service listener used to enter the pin if necessary 
 * @author GGUNTHER
 */
@Named
public class AndroidTvPinListener extends AbstractEventListener<AndroidTvPinEventData> {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(AndroidTvPinListener.class);

	private final PinHandler pinHandler;

	public AndroidTvPinListener(PinHandler pinHandler, EventBusService eventBusService) {
		super(AndroidTvPinEventData.eventType, AndroidTvPinEventData.class, eventBusService);
		this.pinHandler = pinHandler;
	}
	
	@Override
	public void onEvent(DistributedEvent event, AndroidTvPinEventData data) {
		LOGGER.info("Received pin event: {}", data.getPin());
		pinHandler.enterPin(data.getPin());
	}
}
