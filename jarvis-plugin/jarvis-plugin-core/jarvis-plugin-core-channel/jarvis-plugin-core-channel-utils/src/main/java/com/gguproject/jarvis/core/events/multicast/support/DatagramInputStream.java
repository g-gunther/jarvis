package com.gguproject.jarvis.core.events.multicast.support;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.DatagramPacket;
import java.util.UUID;

import com.gguproject.jarvis.core.events.multicast.MulticastManagerStats;
import com.gguproject.jarvis.core.events.support.SocketUtils;

/**
 * Allows to read binary data from UDP datagrams.
 */
public final class DatagramInputStream implements Closeable {

	/** Data input stream that is wrapped around the datagram's raw buffer. */
	private final DataInputStream dis;

	/**
	 * Creates a new {@code DatagramInputStream} that allows to read binary data from a UDP datagram.
	 *
	 * @param packet
	 *            The UDP datagram packet to read.
	 * @param stats
	 *            Global statistics.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public DatagramInputStream(DatagramPacket packet, MulticastManagerStats stats) throws IOException {
		byte[] buffer = packet.getData();
		int offset = packet.getOffset();
		int length = packet.getLength();

		// Check arguments.
		if (buffer == null) {
			stats.incReceivedBadPacket();
			throw new IOException("No data in datagram packet");
		}
		if (offset < 0 || offset > buffer.length || offset > length) {
			stats.incReceivedBadPacket();
			throw new IOException("Bad offset for datagram packet: " + offset);
		}
		if (length < 0 || length > buffer.length) {
			stats.incReceivedBadPacket();
			throw new IOException("Bad length for datagram packet: " + length);
		}

		if (length < DatagramStream.DATAGRAM_MIN_LENGTH) {
			stats.incReceivedShortPacket();
			throw new IOException("Short datagram packet: expected at least " + DatagramStream.DATAGRAM_MIN_LENGTH + " bytes, got " + length
					+ " byte(s)");
		}

		controlChecksum(buffer, offset, length, stats);

		this.dis = new DataInputStream(new ByteArrayInputStream(buffer, offset, length));
	}

	/**
	 * Compute the checksum of an incoming datagram packet.
	 *
	 * @param buffer
	 *            Raw data of the UDP datagram packet to check.
	 * @param offset
	 *            Offset in the buffer.
	 * @param length
	 *            Length of the buffer.
	 * @param stats
	 *            Global statistics bean, updated if an error occurs.
	 * @throws IOException
	 *             If the datagram packet is corrupted.
	 */
	private static void controlChecksum(byte[] buffer, int offset, int length, MulticastManagerStats stats) throws IOException {
		/*
		 * Compute packet checksum. We leave the last 8 bytes, because they contain the stored packet checksum that was computed when it was built (a
		 * long). If the datagram was truncated during transfer, it is very likely that its checksum will be wrong, and thus it will be discarded.
		 */
		if (length < 8) {
			stats.incReceivedBadPacket();
			throw new IOException("Short data. Packet was corrupted!");
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(buffer, offset + (length - 8), 8);
		DataInputStream dis = new DataInputStream(bis);

		long localChecksum = DatagramStream.computeChecksum(buffer, offset, length - 8);
		long storedChecksum = dis.readLong();
		if (localChecksum != storedChecksum) {
			stats.incReceivedBadCRC();
			throw new IOException("Bad CRC. Expected " + Long.toHexString(localChecksum) + ", got " + Long.toHexString(storedChecksum)
					+ ". Packet was corrupted!");
		}
	}

	/**
	 * Close this input stream.
	 *
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	@Override
	public void close() throws IOException {
		dis.close();
	}

	/**
	 * Read a single byte from the datagram packet.
	 *
	 * @return Byte value.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public int readByte() throws IOException {
		return dis.readByte();
	}

	/**
	 * Read a {@code short} from the datagram packet.
	 *
	 * @return Short value.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public short readShort() throws IOException {
		return dis.readShort();
	}

	/**
	 * Read an {@code int} from the datagram packet.
	 *
	 * @return Integer value.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public int readInt() throws IOException {
		return dis.readInt();
	}

	/**
	 * Read a {@code long} from the datagram packet.
	 *
	 * @return Long value.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public long readLong() throws IOException {
		return dis.readLong();
	}

	/**
	 * Read a {@code String} from the datagram packet.
	 *
	 * @return String value, or <code>null</code> if the string was <code>null</code>.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public String readString() throws IOException {
		String s = dis.readUTF();
		if (s != null && s.length() == 0) {
			s = null;
		}
		return s;
	}

	/**
	 * Read a {@code UUID} from the datagram packet.
	 *
	 * @return UUID value.
	 * @throws IOException If an I/O error occurs.
	 */
	public UUID readUUID() throws IOException {
		byte[] bytes = new byte[36];
		dis.read(bytes);
		return UUID.fromString(new String(bytes));
	}
	
	/**
	 * Read an ipv4 address
	 * @return IPv4 address
	 * @throws IOException
	 */
	public String readIpv4Address() throws IOException {
		byte[] bytes = new byte[15];
		dis.read(bytes);
		return SocketUtils.convertBytesToIpv4Address(bytes);
	}

	/**
	 * Read a single {@code Object} from the datagram packet.
	 *
	 * @return Object value, or <code>null</code> if the object was <code>null</code>.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public Object readObject() throws IOException {
		try {
			ObjectInputStream ois = new ObjectInputStream(dis);
			return ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} catch (InvalidClassException e) {
			throw new IOException(e);
		} catch (StreamCorruptedException e) {
			throw new IOException(e);
		} catch (OptionalDataException e) {
			throw new IOException(e);
		}
	}
}
