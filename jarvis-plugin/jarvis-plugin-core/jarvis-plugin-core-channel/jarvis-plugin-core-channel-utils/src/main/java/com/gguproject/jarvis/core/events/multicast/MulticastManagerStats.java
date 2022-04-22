package com.gguproject.jarvis.core.events.multicast;

import java.io.Serializable;

/**
 * Data structure that holds many statistics regarding the multicast distributed events manager.
 */
public final class MulticastManagerStats implements Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = -6062663682107387131L;

	/** Number of packets received with the wrong checksum. */
	private long receivedBadCRC;

	/** Number of bad events received. */
	private long receivedBadEvent;

	/** Number of bad packets received. */
	private long receivedBadPacket;

	/** Number of received packets. */
	private long receivedPackets;

	/** Number of short packets received. */
	private long receivedShortPacket;

	/** Total size of received packets, in bytes. */
	private long receivedSize;

	/** Number of I/O errors when receiving packets. */
	private long receiveFailures;

	/** Number of packet send errors. */
	private long sendFailures;

	/** Number of packets that could not be sent because they overflow the size of a UDP datagram. */
	private long sendPacketWouldOverflow;

	/** Number of sent packets. */
	private long sentPackets;

	/** Total size of sent packets, in bytes. */
	private long sentSize;

	/**
	 * Creates an empty data structure.
	 */
	public MulticastManagerStats() {
		// Empty
	}

	/**
	 * Creates a copy of the data structure.
	 *
	 * @param stats
	 *            Data structure to copy.
	 */
	public MulticastManagerStats(MulticastManagerStats stats) {
		this.receivedBadCRC = stats.receivedBadCRC;
		this.receivedBadEvent = stats.receivedBadEvent;
		this.receivedBadPacket = stats.receivedBadPacket;
		this.receivedPackets = stats.receivedPackets;
		this.receivedShortPacket = stats.receivedShortPacket;
		this.receivedSize = stats.receivedSize;
		this.receiveFailures = stats.receiveFailures;

		this.sendFailures = stats.sendFailures;
		this.sendPacketWouldOverflow = stats.sendPacketWouldOverflow;
		this.sentPackets = stats.sentPackets;
		this.sentSize = stats.sentSize;
	}

	/**
	 * Get the number of packets that were received with the wrong checksum.
	 *
	 * @return Number of packets received with the wrong checksum.
	 */
	public long getReceivedBadCRC() {
		return receivedBadCRC;
	}

	/**
	 * Increment the number of packets received with the wrong checksum.
	 */
	public void incReceivedBadCRC() {
		this.receivedBadCRC++;
	}

	/**
	 * Get the number of bad events received.
	 *
	 * @return Number of bad events received.
	 */
	public long getReceivedBadEvent() {
		return receivedBadEvent;
	}

	/**
	 * Increment the number of bad events received.
	 */
	public void incReceivedBadEvent() {
		this.receivedBadEvent++;
	}

	/**
	 * Get the number of bad packets received.
	 *
	 * @return Number of bad packets received.
	 */
	public long getReceivedBadPacket() {
		return receivedBadPacket;
	}

	/**
	 * Increment the number of bad packets received.
	 */
	public void incReceivedBadPacket() {
		this.receivedBadPacket++;
	}

	/**
	 * Get the number of received packets.
	 *
	 * @return Number of received packets.
	 */
	public long getReceivedPackets() {
		return receivedPackets;
	}

	/**
	 * Increment the number of received packets.
	 */
	public void incReceivedPackets() {
		this.receivedPackets++;
	}

	/**
	 * Get the number of received packets that were too short.
	 *
	 * @return Number of short packets received.
	 */
	public long getReceivedShortPacket() {
		return receivedShortPacket;
	}

	/**
	 * Increment the number of short packets received.
	 */
	public void incReceivedShortPacket() {
		this.receivedShortPacket++;
	}

	/**
	 * Get the total received size, in bytes.
	 *
	 * @return Total received size.
	 */
	public long getReceivedSize() {
		return receivedSize;
	}

	/**
	 * Add a value to the total number of received bytes.
	 *
	 * @param receiveSize
	 *            Value to add
	 */
	public void addReceivedSize(int receiveSize) {
		this.receivedSize += receiveSize;
	}

	/**
	 * Get the number of I/O errors when receiving packets.
	 *
	 * @return Number of I/O errors when receiving packets.
	 */
	public long getReceiveFailures() {
		return receiveFailures;
	}

	/**
	 * Increment the number of I/O errors when receiving packets.
	 */
	public void incReceiveFailures() {
		this.receiveFailures++;
	}

	/**
	 * Get the number of I/O errors when sending packets.
	 *
	 * @return Number of I/O errors when sending packets.
	 */
	public long getSendFailures() {
		return sendFailures;
	}

	/**
	 * Increment the number of I/O errors when sending packets.
	 */
	public void incSendFailures() {
		this.sendFailures++;
	}

	/**
	 * Get the number of packets that could not be sent because they overflowed the size of a UDP datagram.
	 *
	 * @return Number of overflowed non-sent packets.
	 */
	public long getSendPacketWouldOverflow() {
		return sendPacketWouldOverflow;
	}

	/**
	 * Increment the number of packets that could not be sent because they overflowed the size of a UDP datagram.
	 */
	public void incSendPacketWouldOverflow() {
		this.sendPacketWouldOverflow++;
	}

	/**
	 * Get the number of sent packets.
	 *
	 * @return Number of sent packets.
	 */
	public long getSentPackets() {
		return sentPackets;
	}

	/**
	 * Increment the number of sent packets.
	 */
	public void incSentPackets() {
		this.sentPackets++;
	}

	/**
	 * Get the total size of sent packets, in bytes.
	 *
	 * @return Total size of sent packets, in bytes.
	 */
	public long getSentSize() {
		return sentSize;
	}

	/**
	 * Add a value to the total number of sent bytes.
	 *
	 * @param sendSize
	 *            Value to add
	 */
	public void addSentSize(int sendSize) {
		this.sentSize += sendSize;
	}
}
