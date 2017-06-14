package ass.starorad.semestralproject.server.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteArrayUtil {

	private static final Logger logger = LoggerFactory.getLogger(ByteArrayUtil.class);

	/**
	 * @param buffer - preallocated buffer with suitable size; allocate with ByteBuffer.allocate() or ByteBuffer.allocateDirect()
	 * @param channel - readable channel
	 * @return ASCII String
	 * @throws IOException 
	 */
	public static String readStringFromChannel(ByteBuffer buffer, SocketChannel channel) throws IOException {
		int limit = buffer.limit();
		
		channel.read(buffer);
		buffer.flip();
		
		byte[] bytes = new byte[buffer.limit()];
		buffer.get(bytes, 0, buffer.limit());
		
		// reset buffer
		buffer.position(0);
		buffer.limit(limit);
		
		// telnet sends ASCII
		return new String(bytes, "ASCII");
	}

	/**
	 * Reads data from a socket channel into a byte array using an intermediate ByteBuffer.
	 * @param buffer buffer to transfer the data
	 * @param channel socket to read from
	 * @return byte array with transported data
	 * @throws IOException
	 */
	public static byte[] readFromChannel(ByteBuffer buffer, SocketChannel channel) throws IOException {
		int limit = buffer.limit();

		channel.read(buffer);
		buffer.flip();

		byte[] bytes = new byte[buffer.limit()];
		buffer.get(bytes, 0, buffer.limit());

		// reset buffer
		buffer.position(0);
		buffer.limit(limit);

		return bytes;
	}


	/**
	 * Checks whether the byte array ends with a given String.
	 * @param bytes byte array
	 * @param string ending
	 * @return boolean
	 */
	public static boolean endsWith(byte[] bytes, String string) {
		if(bytes == null || string == null || bytes.length < string.length()) {
			return false;
		}

		byte[] end;
		try {
			end = string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
		  logger.error("Unable to encode string into byte array, wrong encoding?", e);
			return false;
		}
		for(int i = 0; i < end.length; i++) {
			if(bytes[bytes.length - 1 - i] != end[end.length - 1 - i]) {
				return false;
			}
		}

		return true;
	}
}
