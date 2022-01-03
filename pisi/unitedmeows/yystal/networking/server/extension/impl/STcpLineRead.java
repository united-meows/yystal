package pisi.unitedmeows.yystal.networking.server.extension.impl;

import pisi.unitedmeows.yystal.clazz.out;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.extension.STcpExtension;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class STcpLineRead extends STcpExtension {

	@Override
	public void onPreDataReceive(YSocketClient client, DataInputStream inputStream, ref<Boolean> cancelDefaultReader, out<byte[]> readData) {
		try {
			String read = inputStream.readLine();
			readData.set(read.getBytes(StandardCharsets.UTF_8));
			cancelDefaultReader.set(true);
		} catch (IOException e) {

		}
	}
}
