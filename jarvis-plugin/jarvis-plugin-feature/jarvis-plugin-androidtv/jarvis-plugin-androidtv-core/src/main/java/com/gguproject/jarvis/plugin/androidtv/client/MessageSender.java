package com.gguproject.jarvis.plugin.androidtv.client;

import java.io.IOException;
import java.io.OutputStream;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.plugin.androidtv.encoder.Key;
import com.gguproject.jarvis.plugin.androidtv.encoder.Key.Code;
import com.gguproject.jarvis.plugin.androidtv.encoder.PacketEncoder;
import com.gguproject.jarvis.plugin.androidtv.util.Message;

/**
 * Send messages to the socket output stream
 */
public class MessageSender {
	private static final Logger LOGGER = AbstractLoggerFactory.getLogger(MessageSender.class);
	
	private PacketEncoder encoder = new PacketEncoder();
	
	private OutputStream outputStream;
	
	public MessageSender(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	public void handleMessage(Message msg) {
		switch (msg.getWhat()) {
			case KEYPRESS:
				LOGGER.info("Send key event: {}", msg);
				this.sendKeyEvent(((Code) msg.getObj()).getNumber(), Key.Action.DOWN.getNumber());
				this.sendKeyEvent(((Code) msg.getObj()).getNumber(), Key.Action.UP.getNumber());
				break;
			default:
				LOGGER.error("Unhandled message: {}", msg);
				break;
		}
	}
	
	public final void sendKeyEvent(int code, int action) {
		sendMessage(this.encoder.encodeKeyEvent(action, code));
    }
	
	public final void configure() {
		LOGGER.debug("Send configuration message");
		sendMessage(this.encoder.encodeConfigure(1080, 1920, (byte) 32, (byte) 3, "f4:5c:89:8d:7c:09"));
	}
	
	public void sendMessage(final byte[] message) {
        if (message.length > 65536) {
            LOGGER.error("Packet size {} exceeds host receive buffer size {}, dropping.", Integer.valueOf(message.length), Integer.valueOf(65536));
            return;
        }
        try {
			this.outputStream.write(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
