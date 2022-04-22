package com.gguproject.jarvis.core.utils;

/**
 * Support functions for hexadecimal data.
 */
public final class Hexadecimal {

	/** Three spaces */
	private static final char[] NO_BYTE = { ' ', ' ', ' ' };

	/** Two spaces */
	private static final char[] SPC = { ' ', ' ' };

	/** Hexadecimal characters. */
	private static final char[] HEX = "0123456789abcdef".toCharArray();
	/**
	 * End-of-line characters used by the Java platform (<code>\r\n</code> on Windows, <code>\n</code> on Unix).
	 */
	private static final String NEW_LINE = System.getProperty("line.separator");

	/** Private constructor -- prevent instantiation */
	private Hexadecimal() {
		// Empty
	}

	/**
	 * Write a text right aligned, padded with zeros.
	 * 
	 * @param sb
	 *            Write buffer.
	 * @param text
	 *            The text to write
	 * @param width
	 *            Field width (number of characters).
	 */
	private static void padWithZero(StringBuilder sb, String text, int width) {
		for (int len = width - text.length(); len > 0; len--) {
			sb.append('0');
		}
		sb.append(text);
	}

	/**
	 * Format the contents of an array of bytes in hexadecimal notation. The contents of the given byte array is written in hexadecimal notation in
	 * the buffer <code>sb</code>, starting from the index <code>offset</code>, and writing no more than <code>length</code> bytes.
	 * <p>
	 * The length of the data is first written on a line by itself, then the data are written. Each line starts with the current offset (in
	 * hexadecimal notation). Then follow 16 bytes of data in hexadecimal. Then these same 16 bytes are written as characters. Non-printable
	 * characters are replaced with a <code>.</code> (dot).
	 * 
	 * @param sb
	 *            Write buffer.
	 * @param data
	 *            Array of bytes.
	 * @param offset
	 *            Offset in the array.
	 * @param length
	 *            Number of bytes to write.
	 */
	public static void formatHexadecimalBuffer(StringBuilder sb, byte[] data, int offset, int length) {
		if (data == null) {
			sb.append("null");
			return;
		}

		if (data.length == 0 || offset < 0 || offset >= data.length || length <= 0) {
			sb.append("empty buffer");
			return;
		}

		int off = offset;
		int len = length;
		int last = off + len;
		if (last > data.length) {
			last = data.length;
			len = data.length - off;
		}

		sb.append(len).append(" byte(s)");

		int max = 16;
		for (int i = off; i < last; i += max) {
			int j;
			int b;
			char c;

			sb.append(NEW_LINE);

			// Write offset
			off = i;
			padWithZero(sb, Integer.toHexString(off), 8);
			sb.append(SPC);

			// Take care of a shorter last line
			if (i + max > last) {
				max = last - i;
			}

			// Dump hex bytes
			for (j = 0; j < max; j++) {
				if (j > 0) {
					sb.append(' ');
				}
				b = data[i + j];
				sb.append(HEX[b >> 4 & 0x0f]);
				sb.append(HEX[b & 0x0f]);
			}

			// Complete with spaces for missing bytes
			for (; j < 16; j++) {
				sb.append(NO_BYTE);
			}

			// Separator (at least two spaces after bytes)
			sb.append(SPC);

			// Dump chars, if possible
			for (j = 0; j < max; j++) {
				b = data[i + j] & 0xff;
				if (b <= 32 || b >= 127) {
					c = '.';
				} else {
					c = (char) b;
				}
				sb.append(c);
			}
		}
	}

	/**
	 * Format the contents of an array of bytes in hexadecimal notation. The contents of the given byte array is written in hexadecimal notation,
	 * starting from the index <code>offset</code>, and writing no more than <code>length</code> bytes.
	 * <p>
	 * The length of the data is first written on a line by itself, then the data are written. Each line starts with the current offset (in
	 * hexadecimal notation). Then follow 16 bytes of data in hexadecimal. Then these same 16 bytes are written as characters. Non-printable
	 * characters are replaced with a <code>.</code> (dot).
	 * 
	 * @param data
	 *            Array of bytes.
	 * @param offset
	 *            Offset in the array.
	 * @param length
	 *            Number of bytes to write.
	 * @return Hexadecimal string.
	 */
	public static String toString(byte[] data, int offset, int length) {
		StringBuilder sb = new StringBuilder();
		formatHexadecimalBuffer(sb, data, offset, length);
		return sb.toString();
	}
}
