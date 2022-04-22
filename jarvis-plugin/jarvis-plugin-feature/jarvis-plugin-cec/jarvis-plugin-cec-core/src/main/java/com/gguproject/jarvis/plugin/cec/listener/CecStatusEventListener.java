package com.gguproject.jarvis.plugin.cec.listener;

import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.bus.EventBusService;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.cec.event.CecStatusRequestEventData;
import com.gguproject.jarvis.plugin.cec.event.CecStatusResponseEventData;
import com.gguproject.jarvis.plugin.cec.event.PowerStatus;
import com.gguproject.jarvis.plugin.cec.service.CecException;
import com.gguproject.jarvis.plugin.cec.service.CecService;

import javax.inject.Named;

/**
 * Service listener used to start the TV 
 * @author GGUNTHER
 */
@Named
public class CecStatusEventListener extends AbstractEventListener<CecStatusRequestEventData> {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(CecStatusEventListener.class);

	private final CecService cecService;

	public CecStatusEventListener(CecService cecService, EventBusService eventBusService) {
		super(CecStatusRequestEventData.eventType, CecStatusRequestEventData.class, eventBusService);
		this.cecService = cecService;
	}
	
	@Override
	public void onEvent(DistributedEvent event, CecStatusRequestEventData data) {
		try {
			PowerStatus powerStatus = this.cecService.getPowerStatus();
			boolean activeSource = this.cecService.isActiveSource();

			this.eventBusService.externalEmit(new CecStatusResponseEventData(powerStatus, activeSource), event.getEventSender());
		} catch (CecException e) {
			LOGGER.error("Error while processing status request", e);
		}
	}
}
