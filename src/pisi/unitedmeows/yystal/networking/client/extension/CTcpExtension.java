package pisi.unitedmeows.yystal.networking.client.extension;

import pisi.unitedmeows.yystal.clazz.out;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;

import java.io.DataInputStream;

public class CTcpExtension {

	protected YTcpClient client;

	public void setup(YTcpClient tcpClient) {
		client = tcpClient;
	}

	public void onConnection(ref<IPAddress> connectionIp, ref<Integer> connectionPort, ref<Boolean> accepted) {

	}

	public void onPostDataReceive(ref<byte[]> data) {

	}

	public void onPreDataReceive(DataInputStream inputStream, ref<Boolean> cancelDefaultReader, out<byte[]> readData) {

	}

	public void onDataSend(ref<byte[]> data, ref<Boolean> send) {

	}

	public void onSocketClose(ref<Boolean> cancel) {

	}

}
