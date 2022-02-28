package pisi.unitedmeows.yystal.networking.server.extension.impl;

import java.io.DataInputStream;

import pisi.unitedmeows.yystal.clazz.out;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.extension.STcpExtension;
import pisi.unitedmeows.yystal.utils.MemoryWriter;

public class STcpFixedSize extends STcpExtension {
	@Override
	public void onPreDataReceive(final YSocketClient client, final DataInputStream inputStream, final ref<Boolean> cancelDefaultReader, final out<byte[]> readData) {
		try {
			int dataSize = inputStream.readInt();
			if (dataSize > 65535) {
				dataSize = 65535;
			}
			final byte[] data = new byte[dataSize];
			inputStream.read(data);
			cancelDefaultReader.set(true);
			readData.set(data);
		}
		catch (final Exception ex) {}
	}

	@Override
	public void onDataSend(final YSocketClient client, final ref<byte[]> data, final ref<Boolean> send) {
		try (MemoryWriter memoryWriter = new MemoryWriter()) {
			final byte[] rawData = data.get();
			memoryWriter.writeInt(rawData.length);
			memoryWriter.write(rawData);
			data.set(memoryWriter.getBytes());
			send.set(true);
		}
		catch (final Exception ex) {}
	}
}
