package test.yystal;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.exception.impl.YexIO;
import pisi.unitedmeows.yystal.file.YFile;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;
import pisi.unitedmeows.yystal.utils.Capsule;
import pisi.unitedmeows.yystal.utils.CoID;
import pisi.unitedmeows.yystal.utils.YRandom;
import pisi.unitedmeows.yystal.utils.kThread;
import pisi.unitedmeows.yystal.web.YWebClient;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pisi.unitedmeows.yystal.parallel.Async.*;
import static pisi.unitedmeows.yystal.YYStal.*;

public enum YTestStart {
	gaming; /* :D */

	public static void main(final String[] args) {
		System.out.println("Starting..");
		System.out.println("=============================");
		int i = 100;
		long firstCheck;

		startWatcher();

		{
			YTcpServer server = new YTcpServer(IPAddress.LOOPBACK, 2173);
			server.listen();
			YTcpClient client = new YTcpClient();
			while (i > 0) {
				client.send(("hello world " + YRandom.BASIC.nextInt(9999)).getBytes(StandardCharsets.UTF_8));
				i--;
				kThread.sleep(50);
			}
			client.close();
			server.close();
		}

		firstCheck = stopWatcher() - 50 * 100 /* remove the amount of time we sleep */;


		System.out.println("Sending random bytes took " + firstCheck + "ms");

		startWatcher();
		int massConnectClientSize = 40;
		{
			YTcpServer server = new YTcpServer(IPAddress.LOOPBACK, 2173);
			server.listen();
			for (int j = massConnectClientSize; j > 0; j--) {
				YTcpClient client = new YTcpClient();
				client.connect(IPAddress.LOOPBACK, 2173);
				client.send("yystal".getBytes(StandardCharsets.UTF_8));
				kThread.sleep(50);
			}
		}
		long secondCheck = stopWatcher() - massConnectClientSize * 50;
		System.out.println("Mass connect check took: (" + massConnectClientSize + " clients) " + secondCheck + "ms");
		System.out.println("=============================");
		kThread.sleep(1099900);
	}


}