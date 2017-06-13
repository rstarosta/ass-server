package ass.starorad.semestralproject.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ByteArrayUtil {
	
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

	public static boolean endsWith(byte[] bytes, String string) {
		if(string == null || bytes.length < string.length()) {
			return false;
		}

		byte[] end;
		try {
			end = string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
