package test.yystal;
import java.io.File;

import java.nio.charset.StandardCharsets;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.exception.YTry;
import pisi.unitedmeows.yystal.logger.impl.YLogger;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;
import pisi.unitedmeows.yystal.utils.YRandom;
import pisi.unitedmeows.yystal.utils.kThread;
import pisi.unitedmeows.yystal.utils.list.YCache;

import static pisi.unitedmeows.yystal.YYStal.*;

public enum YTestStart {
	gaming; /* :D */
	private static final YLogger logger = YYStal.createLogger(YTestStart.class)
			.time(YLogger.Time.DAY_MONTH_YEAR_FULL)
			.colored(true);

	public static void main(final String[] args) {

        YCache<String, Integer> cache = new YCache<>(2000);
        cache.put("slowcheet4h", 2173);
        kThread.sleep(1000);
        cache.put("ghost", 2000);
        System.out.println(cache.size());
        System.out.println("erik: " + cache.get("erik"));
        System.out.println("ghost: " + cache.get("ghost"));
        kThread.sleep(1500);
        System.out.println(cache.size());
        System.out.println("ghost: " + cache.get("ghost"));
		kThread.sleep(1000);

		if (true) {
			return;
		}

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
