package test.yystal;

import java.awt.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.logger.impl.YLogger;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;
import pisi.unitedmeows.yystal.ui.YWindow;
import pisi.unitedmeows.yystal.ui.element.YLabel;
import pisi.unitedmeows.yystal.ui.element.simple.YBackgroundColor;
import pisi.unitedmeows.yystal.ui.element.simple.YBackgroundImage;
import pisi.unitedmeows.yystal.utils.YRandom;
import pisi.unitedmeows.yystal.utils.kThread;

import javax.imageio.ImageIO;

import static pisi.unitedmeows.yystal.YYStal.*;

public enum YTestStart {
	gaming; /* :D */
	private static final YLogger logger = YYStal.createLogger(YTestStart.class)
			.time(YLogger.Time.DAY_MONTH_YEAR_FULL)
			.colored(true);

	public static void main(final String[] args) throws Exception {

        YWindow ywindow = new YWindow("yystal", 640, 480);
        ywindow.open();

        ywindow.background.set(() -> new YBackgroundColor(Color.WHITE));

        YLabel yLabel = new YLabel();
        yLabel.text.set("Hello world");
        ywindow.addElement(yLabel);



        kThread.sleep(1000000);
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
