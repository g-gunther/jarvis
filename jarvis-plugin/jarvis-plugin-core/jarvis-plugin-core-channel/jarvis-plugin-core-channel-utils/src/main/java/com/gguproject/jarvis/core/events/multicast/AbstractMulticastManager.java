package com.gguproject.jarvis.core.events.multicast;

import com.gguproject.jarvis.core.ApplicationConfiguration;
import com.gguproject.jarvis.core.bean.lifecycle.OnPostConstruct;
import com.gguproject.jarvis.core.bus.support.DistributedEvent;
import com.gguproject.jarvis.core.events.multicast.support.DatagramOutputStream;
import com.gguproject.jarvis.core.events.multicast.support.MulticastRequestEvent;
import com.gguproject.jarvis.core.events.support.AbstractSocketManager;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.Hexadecimal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Events manager that propagates events to a multicast IP address.
 */
public abstract class AbstractMulticastManager extends AbstractSocketManager implements OnPostConstruct {
	/** Log */
	private static final Logger LOG = AbstractLoggerFactory.getLogger(AbstractMulticastManager.class);

	/** Error message */
	private static final String MSG_DISABLED = "Distributed events management is disabled until you resolve the issue.";

	/** Global statistics. */
	private final MulticastManagerStats stats = new MulticastManagerStats();

	/** Multicast IP address. */
	private String addressString = "224.0.0.1";

	/** IP port number (1..65535). */
	private int port = 9999;

	/** Multicast IP addess. */
	private InetAddress group;

	/** Multicast socket. */
	private MulticastSocket socket;

	public AbstractMulticastManager(ApplicationConfiguration applicationConfiguration){
		super(applicationConfiguration);
	}

	/**
	 * Initialize the events manager. Parse the {@link #setAddress address} and {@link #setPort port} that
	 * were configured , and try to bind a multicast socket to that address.
	 *
	 * @throws IOException
	 *             If a network error occurs.
	 */
	@Override
	public void postConstruct() {
		/*
		 * Parse the address. If this is not a valid multicast address, we leave a note in the logs and
		 * proceed with events management disabled.
		 */
		try {
			this.group = InetAddress.getByName(this.addressString);
			if (!this.group.isMulticastAddress()) {
				throw new IOException(
						"First four bits of address must be 1110 in case of IPv4, or first byte must be 11111111 in case of IPv6.");
			}
		} catch (IOException e) {
			LOG.error("IP address {}:{} is not a valid multicast address: {}. {}", this.addressString, this.port,
					e.getMessage(), MSG_DISABLED);
			LOG.debug("Error cause", e);
			this.socket = null;
			return;
		}

		LOG.info("Starting multicast events manager on {}:{}", this.addressString, this.port);
		
		/*
		 * Create a multicast socket. This may fail for several reasons: wrong system configuration, multicast
		 * explicitly disabled by System Administrator, etc. In such a case, we leave a note in the logs and
		 * proceed with events management disabled.
		 * If fails, try with VM arguments: -Djava.net.preferIPv4Stack=true
		 */
		try {
			this.socket = new MulticastSocket(this.port);

			// Join group
			this.socket.joinGroup(this.group);
		} catch (IOException e) {
			LOG.error(
					"Cannot join multicast group {}:{}: {}. Please verify the validity of the address (must be multicast) and verify your network configuration (is multicast enabled on your network interfaces?). {}",
					this.addressString, this.port, e.getMessage(), MSG_DISABLED);
			LOG.debug("Error cause", e);
			this.socket = null;
		}
		
		this.initialize();
	}
	
	/**
	 * Post initialization to be overridden
	 */
	protected void initialize() {
	}

	/**
	 * Called in order to destroy the bean.
	 */
	public void close() {
		LOG.info("Stopping the multicast events manager");

		// Close the datagram socket
		try {
			if (this.socket != null && this.group != null) {
				this.socket.leaveGroup(this.group);
				this.socket.close();
				this.socket = null;
				this.group = null;
			}
		} catch (IOException e) {
			LOG.warn("Error while closing multicast socket", e);
		}
	}
	
	/**
	 * Send a multicast event.
	 * @param event The event to send.
	 */
	protected void sendEvent(MulticastRequestEvent event) {
		try {
			// Build a datagram from the event's data
			try (DatagramOutputStream out = new DatagramOutputStream(DistributedEvent.DEFAULT_DATAGRAM_SIZE)) {
				event.writeTo(out);

				// Fail if the datagram is too big
				if (out.isOverflowed()) {
					LOG.warn(
							"Cannot send datagram packet for event {} because it is too big ({} bytes), while the send buffer size is limited to {} bytes.",
							event, out.getSize(), out.getMaxSize());
					this.stats.incSendPacketWouldOverflow();
					return;
				}

				DatagramPacket packet = out.getDatagramPacket();

				LOG.debug("Event to send: {}", event);
				if (LOG.isTraceEnabled()) {
					LOG.trace("Raw packet data: {}",
							Hexadecimal.toString(packet.getData(), packet.getOffset(), packet.getLength()));
				}

				// Send packet and update stats.
				packet.setAddress(this.group);
				packet.setPort(this.port);
				this.socket.send(packet);
				this.stats.incSentPackets();
				this.stats.addSentSize(packet.getLength());

				LOG.debug("Datagram packet sent");
			}
		} catch (IOException e) {
			this.stats.incSendFailures();
			LOG.error("Cannot send datagram", e);
		}
	}
	
	public MulticastSocket getSocket(){
		return this.socket;
	}
	
	public MulticastManagerStats getStats(){
		return this.stats;
	}
	
	/**
	 * Define the IP multicast address where the events will be sent to / received from.
	 * @param address Multicast IP address.
	 */
	public void setAddress(String address) {
		this.addressString = address;
	}

	/**
	 * Define the IP port where the events will be sent to / received from.
	 * @param port IP port number (1..65535).
	 */
	public void setPort(int port) {
		this.port = port;
	}
}
