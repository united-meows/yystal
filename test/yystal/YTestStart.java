package test.yystal;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.glfw.GLFW;
import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.logger.impl.YLogger;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;
import pisi.unitedmeows.yystal.parallel.utils.YTimer;
import pisi.unitedmeows.yystal.ui.YUI;
import pisi.unitedmeows.yystal.ui.YWindow;
import pisi.unitedmeows.yystal.ui.element.YContainer;
import pisi.unitedmeows.yystal.ui.element.YLabel;
import pisi.unitedmeows.yystal.ui.element.impl.YImageBox;
import pisi.unitedmeows.yystal.ui.element.simple.YBackgroundColor;
import pisi.unitedmeows.yystal.ui.element.simple.YBackgroundImage;
import pisi.unitedmeows.yystal.ui.events.KeyDownEvent;
import pisi.unitedmeows.yystal.ui.events.KeyUpEvent;
import pisi.unitedmeows.yystal.ui.events.MouseEvent;
import pisi.unitedmeows.yystal.ui.utils.Vertex2f;
import pisi.unitedmeows.yystal.utils.Vector2f;
import pisi.unitedmeows.yystal.utils.YRandom;
import pisi.unitedmeows.yystal.utils.kThread;
import pisi.unitedmeows.yystal.web.YWebClient;

import javax.imageio.ImageIO;

import static pisi.unitedmeows.yystal.YYStal.*;

public enum YTestStart {
	gaming; /* :D */
	private static final YLogger logger = YYStal.createLogger(YTestStart.class)
			.time(YLogger.Time.DAY_MONTH_YEAR_FULL)
			.colored(true);

    private static int sideway;
    private static int vertical;

	public static void main(final String[] args) throws Exception {

        YWindow ywindow = new YWindow("yystal", 640, 480);
        ywindow.resizable.set(() -> true);
        ywindow.open();

        ywindow.background.set(new YBackgroundColor(Color.WHITE));

        YContainer container = new YContainer(new Vertex2f(50, 50), new Vector2f(50, 50));
        container.background.set(new YBackgroundColor(Color.GREEN));
        ywindow.addElement(container);

        ywindow.mouseEvent.bind(new MouseEvent() {
            @Override
            public void onMouseClick(Button button, Action action, int mods) {
                if (action == Action.PRESS) {
                    container.location().setX(currentWindow().mouseX());
                    container.location().setY(currentWindow().mouseY());
                }
            }
        });


        YLabel yLabel = new YLabel();

        new YTimer(50) {
            @Override
            public void tick() {
                yLabel.text.set(String.valueOf(ywindow.keyManager().isPressed(GLFW.GLFW_KEY_A)));
            }
        }.start();

        yLabel.location(new Vertex2f(5, 40));
        ywindow.addElement(yLabel);


        while (ywindow != null) {
            kThread.sleep(1000);
        }


        kThread.sleep(1000000);

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

    static String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static String randomId() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            result.append(characters.charAt(YRandom.BASIC.nextInt(characters.length())));
        }
        return result.toString();
    }
}
