package pisi.unitedmeows.yystal.networking.server.extension.impl;

import pisi.unitedmeows.yystal.clazz.out;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.extension.STcpExtension;
import pisi.unitedmeows.yystal.utils.MemoryWriter;

import java.io.DataInputStream;

public class STcpFixedSize extends STcpExtension {

	@Override
	public void onPreDataReceive(YSocketClient client, DataInputStream inputStream, ref<Boolean> cancelDefaultReader, out<byte[]> readData) {
		try {
			System.out.println("manipulation the data");
			int dataSize = inputStream.readInt();
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
			memoryWriter.write((int) rawData.length);
			memoryWriter.write(rawData);
			data.set(memoryWriter.getBytes());
			send.set(true);
		} catch (Exception ex) {
		}
	}
}
