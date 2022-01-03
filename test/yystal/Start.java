package test.yystal;

import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.client.extension.impl.CTcpLineRead;
import pisi.unitedmeows.yystal.networking.events.CDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.events.SConnectionReceivedEvent;
import pisi.unitedmeows.yystal.networking.events.SDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;
import pisi.unitedmeows.yystal.networking.server.extension.impl.STcpLineRead;
import pisi.unitedmeows.yystal.ui.YWindow;
import pisi.unitedmeows.yystal.utils.kThread;

import java.nio.charset.StandardCharsets;

import static pisi.unitedmeows.yystal.parallel.Async.*;

public enum Start {
	gaming; /* :D */

	public static void main(final String[] args) {
		YTcpServer tcpServer = new YTcpServer(IPAddress.LOOPBACK, 2173);
		tcpServer.connectionReceivedEvent.bind(new SConnectionReceivedEvent() {
			@Override
			public void onClientConnected(YSocketClient socketClient) {
				System.out.println("someone connected");
			}
		});
		tcpServer.dataReceiveEvent.bind(new SDataReceivedEvent() {
			@Override
			public void onClientDataReceived(YSocketClient client, byte[] data) {
				System.out.println("Server Received: " + new String(data));
			}
		});

		tcpServer.registerExtension(new STcpLineRead());
		tcpServer.listen();
		async_loop(() -> {
			tcpServer.connectedClients().forEach(client -> client.write("Hello client\n".getBytes(StandardCharsets.UTF_8)));
		}, 1000);

		YTcpClient client = new YTcpClient();
		client.registerExtension(new CTcpLineRead());
		client.connect(IPAddress.LOOPBACK, 2173);

		client.dataReceivedEvent.bind(new CDataReceivedEvent() {
			@Override
			public void onDataReceived(byte[] data) {
				System.out.println("Client Received: " + new String(data));
			}
		});
		async_loop(() -> {
			client.send("Hello server!\n".getBytes(StandardCharsets.UTF_8));
		}, 1000);
		kThread.sleep(500000 );

	}
}