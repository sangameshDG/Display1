package network;

import java.nio.ByteBuffer;

public class LiquidStream {

	private ByteBuffer dataBuf;

	public LiquidStream(final byte[] dataStream) {
		dataBuf = ByteBuffer.wrap(dataStream);
	}

	public ByteBuffer getAsterixBuffer() {
		return dataBuf;
	}

}
