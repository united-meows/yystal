package pisi.unitedmeows.yystal.networking.client.extension.impl;

import pisi.unitedmeows.yystal.clazz.out;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.client.extension.CTcpExtension;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CTcpLineRead extends CTcpExtension {


	protected BufferedReader bufferedReader;

	@Override
	public void onPreDataReceive(DataInputStream inputStream, ref<Boolean> cancelDefaultReader, out<byte[]> readData) {
		if (bufferedReader == null) {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		}

		try {
			String readLine = bufferedReader.readLine();
			cancelDefaultReader.set(true);
			readData.set(readLine.getBytes(StandardCharsets.UTF_8));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
