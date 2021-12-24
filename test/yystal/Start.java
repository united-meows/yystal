package test.yystal;


import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.events.SDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;
import pisi.unitedmeows.yystal.parallel.Future;
import pisi.unitedmeows.yystal.utils.kThread;
import pisi.unitedmeows.yystal.web.YWebClient;

import static pisi.unitedmeows.yystal.YYStal.*;
import static pisi.unitedmeows.yystal.parallel.Async.*;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Start {

	public static void main(String[] args) {
		YTcpServer tcpServer = new YTcpServer(IPAddress.ANY, 2173).makeFixed();
		tcpServer.listen();
		tcpServer.dataReceiveEvent.bind(new SDataReceivedEvent() {
			@Override
			public void onClientDataReceived(YSocketClient client, byte[] data) {
				System.out.println(new String(data));
			}
		});

		YTcpClient tcpClient = new YTcpClient().makeFixed();
		tcpClient.connect(IPAddress.ANY, 2173);

		tcpClient.send("Hello Server".getBytes(StandardCharsets.UTF_8));
		kThread.sleep(100000);

	}

}
