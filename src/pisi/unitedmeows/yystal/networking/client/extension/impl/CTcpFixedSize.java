package pisi.unitedmeows.yystal.networking.client.extension.impl;

import pisi.unitedmeows.yystal.clazz.out;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.networking.client.extension.CTcpExtension;
import pisi.unitedmeows.yystal.utils.MemoryWriter;

import java.io.DataInputStream;
import java.io.IOException;

public class CTcpFixedSize extends CTcpExtension {

	@Override
	public void onDataSend(ref<byte[]> data, ref<Boolean> send) {
		MemoryWriter memoryWriter = new MemoryWriter();

		try {
			memoryWriter.writeInt(data.get().length);
			memoryWriter.write(data.get());
		} catch (Exception ex) {

		}
		data.set(memoryWriter.getBytes());
		send.set(true);
		try {
			memoryWriter.close();
		} catch (IOException e) {}
	}

	@Override
	public void onPreDataReceive(DataInputStream inputStream, ref<Boolean> cancelDefaultReader, out<byte[]> readData) {
		try {
			int size = inputStream.readInt();
			byte[] received = new byte[size];
			inputStream.read(received);
			readData.set(received);
			cancelDefaultReader.set(true);
		} catch (Exception ex) {
		}
	}
}
