package test.yystal;

import static pisi.unitedmeows.yystal.YYStal.*;

import java.nio.charset.StandardCharsets;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.logger.impl.YLogger;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;
import pisi.unitedmeows.yystal.utils.YRandom;
import pisi.unitedmeows.yystal.utils.kThread;

public enum YTestStart {
	gaming; /* :D */

	private static final YLogger logger = YYStal.createLogger(YTestStart.class).setTime(YLogger.Time.DAY_MONTH_YEAR_FULL).setColored(true);

	public static void main(final String[] args) {
		logger.fatal("ez");
		System.out.println("naber 1");
		logger.info("ez");
		System.out.println("naber 2");
		logger.debug("ez");
		System.out.println("naber 3");
		if (true) return;
		System.out.println("Starting..");
		System.out.println("=============================");
		int i = 100;
		long firstCheck;
		startWatcher();
		{
			final YTcpServer server = new YTcpServer(IPAddress.LOOPBACK, 2173);
			server.listen();
			final YTcpClient client = new YTcpClient();
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
		final int massConnectClientSize = 40;
		{
			final YTcpServer server = new YTcpServer(IPAddress.LOOPBACK, 2173);
			server.listen();
			for (int j = massConnectClientSize; j > 0; j--) {
				final YTcpClient client = new YTcpClient();
				client.connect(IPAddress.LOOPBACK, 2173);
				client.send("yystal".getBytes(StandardCharsets.UTF_8));
				kThread.sleep(50);
			}
		}
		final long secondCheck = stopWatcher() - massConnectClientSize * 50; /* remove the amount of time we sleep */
		System.out.println("Mass connect check took: (" + massConnectClientSize + " clients) " + secondCheck + "ms");
		System.out.println("=============================");
		kThread.sleep(1099900);
	}
}
