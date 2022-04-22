package com.gguproject.jarvis.master.channel;

import com.gguproject.jarvis.core.ApplicationConfiguration;
import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.bus.AbstractEventListener;
import com.gguproject.jarvis.core.events.tcp.AbstractTcpReceiver;
import com.gguproject.jarvis.core.events.tcp.support.TcpResponseEvent;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.google.gson.Gson;

import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

@Named
public class TcpCommandReceiver extends AbstractTcpReceiver implements OnPostConstruct {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(TcpCommandReceiver.class);
	
	private static Gson gson = new Gson();

	private final ApplicationConfiguration configuration;

	private final List<AbstractEventListener<?>> eventListeners;

	public TcpCommandReceiver(ApplicationConfiguration configuration,
		List<AbstractEventListener<?>> eventListeners) {
		super("SOCKET_COMMAND_RECEIVER");
		this.configuration = configuration;
		this.eventListeners = eventListeners;
	}

	public void postConstruct() {
		this.setPort(this.configuration.getIntProperty(ApplicationConfiguration.PropertyKey.tcpListenPort));
	}
	
	protected void onMessage(TcpResponseEvent event) {
		String eventType = event.getData().substring(0, event.getData().indexOf('@'));
		String eventData = event.getData().substring(event.getData().indexOf('@') + 1);
		LOGGER.debug("Deserialize event from class: {} with data: {}", eventType, eventData);
		
		List<AbstractEventListener<?>> listeners = this.eventListeners.stream().filter(l -> l.getEventType().equals(eventType)).collect(Collectors.toList());
		
		if(!listeners.isEmpty()) {
			listeners.forEach(listener -> {
					LOGGER.debug("Call listener {}", listener);
					try {
						listener.parseAndProcess(event, eventData);
					} catch (Throwable t) {
						LOGGER.error("Error while processing listener {}", listener, t);
					}
			});
		} else {
			LOGGER.info("Can't find any listeners for type {}", eventType);
		}
	}
}
