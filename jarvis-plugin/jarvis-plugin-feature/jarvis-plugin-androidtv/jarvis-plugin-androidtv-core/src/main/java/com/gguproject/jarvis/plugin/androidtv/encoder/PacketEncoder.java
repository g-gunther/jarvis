package com.gguproject.jarvis.plugin.androidtv.encoder;


import java.nio.ByteBuffer;

public class PacketEncoder {
    long mIndex = 0;

    public byte[] encodeConfigure(int width, int height, byte maxPointers, byte inputMode, String physAddr) {
        PacketBuilder builder = new PacketBuilder((byte) 0);
        builder.putInt(width).putInt(height).put(maxPointers).put(inputMode).put((byte) 0).put((byte) 0).putCharSequence(physAddr);
        return builder.build();
    }

    public byte[] encodeKeyEvent(int action, int code) {
        long j = this.mIndex;
        this.mIndex = 1 + j;
        return encodeKeyEvent(j, action, code);
    }

    public byte[] encodeKeyEvent(long index, int action, int code) {
        PacketBuilder builder = new PacketBuilder((byte) 2);
        builder.putLong(index).putInt(action).putInt(code);
        return builder.build();
    }
 
    
    private class PacketBuilder {
		private final ByteBuffer buffer = ByteBuffer.allocate(65539);
	
	    public PacketBuilder(byte messageType) {
	        this.buffer.clear();
	        addHeader(messageType);
	    }
	
	    public PacketBuilder put(byte[] src) {
	        this.buffer.put(src);
	        return this;
	    }
	
	    public PacketBuilder put(byte value) {
	        this.buffer.put(value);
	        return this;
	    }
	
	    public PacketBuilder putShort(short value) {
	        this.buffer.putShort(value);
	        return this;
	    }
	
	    public PacketBuilder putInt(int value) {
	        this.buffer.putInt(value);
	        return this;
	    }
	
	    public PacketBuilder putLong(long value) {
	        this.buffer.putLong(value);
	        return this;
	    }
	
	    public PacketBuilder putFloat(float value) {
	        this.buffer.putFloat(value);
	        return this;
	    }
	
	    public PacketBuilder putCharSequence(CharSequence sequence) {
	        if (sequence == null) {
	            this.buffer.put((byte) 1);
	        } else {
	            byte[] payload = sequence.toString().getBytes();
	            this.buffer.put((byte) 0);
	            this.buffer.putInt(payload.length);
	            this.buffer.put(payload);
	        }
	        return this;
	    }
	
	    public byte[] build() {
	        setPayloadSize();
	        byte[] packet = new byte[this.buffer.position()];
	        System.arraycopy(this.buffer.array(), this.buffer.arrayOffset(), packet, 0, this.buffer.position());
	        return packet;
	    }
	
	    public void destroy() {
	        this.buffer.clear();
	    }
	
	    private void addHeader(byte messageType) {
	        put((byte) 1).put(messageType).putShort((short) 0);
	    }
	
	    private void setPayloadSize() {
	        this.buffer.putShort(2, (short) (this.buffer.position() - 4));
	    }
    }
}
