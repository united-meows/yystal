package pisi.unitedmeows.yystal.networking.server.extension.impl;

import pisi.unitedmeows.yystal.clazz.out;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.extension.STcpExtension;
import pisi.unitedmeows.yystal.utils.MemoryWriter;

import java.io.DataInputStream;
import java.io.IOException;

public class STcpFixedSize extends STcpExtension {

	@Override
	public void onPreDataReceive(YSocketClient client, DataInputStream inputStream, ref<Boolean> cancelDefaultReader, out<byte[]> readData) {
		try {
			int dataSize = inputStream.readInt();
			if (dataSize > 65535) {
				dataSize = 65535;
			}
			byte[] data = new byte[dataSize];
			inputStream.read(data);
			cancelDefaultReader.set(true);
			readData.set(data);

		} catch (Exception ex) {
		}
	}

	@Override
	public void onDataSend(YSocketClient client, ref<byte[]> data, ref<Boolean> send) {
		try {
			byte[] rawData = data.get();
			MemoryWriter memoryWriter = new MemoryWriter();
			memoryWriter.writeInt(rawData.length);
			memoryWriter.write(rawData);
			data.set(memoryWriter.getBytes());
			send.set(true);
			try {
				memoryWriter.close();
			} catch (IOException e) {}
		} catch (Exception ex) {
		}
	}
}
