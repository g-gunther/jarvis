package com.gguproject.jarvis.core.events.multicast.support;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.util.UUID;

import com.gguproject.jarvis.core.events.support.SocketUtils;

/**
 * Allows to write binary data in order to build UDP datagrams.
 */
public final class DatagramOutputStream implements Closeable {
	/** Max size of the datagram being buit */
	private final int maxSize;

	/** Buffer that contains the raw data of the datagram packet. */
	private final ByteArrayOutputStream bos;

	/** Data output stream wrapped around the buffer. */
	private final DataOutputStream dos;

	/**
	 * Creates a new datagram output stream.
	 * 
	 * @param datagramSize
	 *            Maximum size expected for the datagram.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public DatagramOutputStream(int datagramSize) throws IOException {
		this.maxSize = datagramSize;
		this.bos = new ByteArrayOutputStream(maxSize);
		this.dos = new DataOutputStream(bos);
	}

	/**
	 * Close this output stream.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	@Override
	public void close() throws IOException {
		// Release streams
		dos.close();
	}

	/**
	 * Build a datagram packet from the data that have been written to the stream. Just after the data, a checksum is computed and stored (on 8
	 * bytes).
	 * 
	 * @return Next datagram packet.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public DatagramPacket getDatagramPacket() throws IOException {
		// Compute checksum.
		byte[] data = bos.toByteArray();
		long checksum = DatagramStream.computeChecksum(data, 0, data.length);

		/*
		 * Write checksum in the last 8 bytes of the datagram. Writing the checksum at this place allows to easily detect packets that were truncated,
		 * because their checksum will be wrong.
		 */
		writeLong(checksum);

		// Get modified array with checksum.
		data = bos.toByteArray();
		
		return new DatagramPacket(data, 0, data.length);
	}

	/**
	 * Check whether the datagram packet would overflow when adding new data. This method can be called before adding data to the packet, in order to
	 * check for overflow. If adding the data would result in a packet overflow, it returns <code>true</code>. This means that no more data can be
	 * added to the packet.
	 * 
	 * @param size
	 *            Size of data to be added, in bytes.
	 * @return <code>true</code> if adding the data would overflow the packet size.
	 */
	public boolean wouldOverflow(int size) {
		return (getSize() + size + 8 > maxSize);
	}

	/**
	 * Test whether the datagram packet being built is bigger than the maximum datagram packet size that was specified in the constructor.
	 * 
	 * @return <code>true</code> if the packet is overflowed.
	 */
	public boolean isOverflowed() {
		return (getSize() + 8 > maxSize);
	}

	/**
	 * Get the current size of the datagram packet's data.
	 * 
	 * @return Size of the data, in bytes.
	 */
	public int getSize() {
		return dos.size();
	}

	/**
	 * Get the maximum size of the datagram packet, as given in the constructor.
	 * 
	 * @return Maximum size expected for the datagram.
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Write a single {@code byte} to the datagram packet.
	 * 
	 * @param i
	 *            The byte value to write.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void writeByte(int i) throws IOException {
		dos.writeByte(i);
	}

	/**
	 * Write a single {@code short} to the datagram packet.
	 * 
	 * @param i
	 *            The short value to write.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void writeShort(short i) throws IOException {
		dos.writeShort(i);
	}

	/**
	 * Write a single {@code int} to the datagram packet.
	 * 
	 * @param i
	 *            The integer value to write.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void writeInt(int i) throws IOException {
		dos.writeInt(i);
	}

	/**
	 * Write a single {@code long} to the datagram packet.
	 * 
	 * @param i
	 *            The long value to write.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void writeLong(long i) throws IOException {
		dos.writeLong(i);
	}

	/**
	 * Write a single {@code String} to the datagram packet.
	 * 
	 * @param str
	 *            The string to write.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void writeString(String str) throws IOException {
		if (str == null) {
			dos.writeUTF("");
		} else {
			dos.writeUTF(str);
		}
	}
	
	/**
	 * Write and ip address
	 * @param ipAddress
	 * @throws IOException
	 */
	public void writeIpv4Address(String ipAddress) throws IOException {
		dos.write(SocketUtils.convertIpv4AddressToBytes(ipAddress));
	}

	/**
	 * Write a single {@code UUID} to the datagram packet. A UUID represents a 128-bit value.
	 * 
	 * @param uuid
	 *            The UUID to write.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void writeUUID(UUID uuid) throws IOException {
		dos.write(uuid.toString().getBytes());
	}

	/**
	 * Write a single {@code Object} to the datagram packet.
	 * 
	 * @param object
	 *            The object to write.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void writeObject(Object object) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(dos);
		oos.writeObject(object);
		oos.flush();
	}
}
