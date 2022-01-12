package pisi.unitedmeows.yystal.networking.server.extension.impl;

import pisi.unitedmeows.yystal.clazz.out;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.clazz.soulbound;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.extension.STcpExtension;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class STcpLineRead extends STcpExtension {

	private soulbound<YSocketClient, BufferedReader> bufferedBound;

	public STcpLineRead() {
		bufferedBound = new soulbound<YSocketClient, BufferedReader>(10000) {
			@Override
			public boolean breakBound(YSocketClient instance, BufferedReader value) {
				return instance.isDisconnected();
			}

			@Override
			public void onRemove(YSocketClient instance, BufferedReader value) {
				try { value.close(); } catch (IOException e) {}
			}
		};
	}

	@Override
	public void onPreDataReceive(YSocketClient client, DataInputStream inputStream, ref<Boolean> cancelDefaultReader, out<byte[]> readData) {
		try {
			String read = bufferedBound.computeIfAbsent(client, i -> new BufferedReader(new InputStreamReader(inputStream))).readLine();
			readData.set(read.getBytes(StandardCharsets.UTF_8));
			cancelDefaultReader.set(true);
		} catch (Exception e) {

		}
	}
}
