package com.gguproject.jarvis.core.events.multicast.support;

import java.util.zip.Adler32;

/**
 * Helper methods for input and output streams that work on datagrams.
 */
public final class DatagramStream {

	/**
	 * Minimum length of a datagram, in bytes. This sum accounts for the header and the trailing checksum:
	 * <ul>
	 * <li>magic number (4 bytes)</li>
	 * <li>version (1 byte)</li>
	 * <li>action (1 byte)</li>
	 * <li>timestamp (8 bytes)</li>
	 * <li>checksum (8 bytes)</li>
	 * </ul>
	 */
	public static final int DATAGRAM_MIN_LENGTH = 22;

	/** Private construtor -- prevent instantiation */
	private DatagramStream() {
		// Empty
	}

	/**
	 * Compute Adler32 checksum of a buffer.
	 * 
	 * @param buffer
	 *            Buffer to be checked.
	 * @param offset
	 *            Start index in the buffer.
	 * @param length
	 *            Length of the buffer.
	 * @return Adler32 checksum of the buffer section specified by the offset and length indexes.
	 */
	public static long computeChecksum(byte[] buffer, int offset, int length) {
		Adler32 checksum = new Adler32();
		checksum.update(buffer, offset, length);
		return checksum.getValue();
	}
}
