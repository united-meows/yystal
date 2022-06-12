package test.yystal;

import java.io.IOException;
import java.net.URL;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.logger.impl.YLogger;
import pisi.unitedmeows.yystal.parallel.repeaters.Repeater;
import pisi.unitedmeows.yystal.ui.YWindow;
import pisi.unitedmeows.yystal.ui.element.impl.YButton;
import pisi.unitedmeows.yystal.ui.element.impl.YLabel;
import pisi.unitedmeows.yystal.ui.element.simple.YBackgroundImage;
import pisi.unitedmeows.yystal.ui.events.ButtonClickEvent;
import pisi.unitedmeows.yystal.ui.utils.Vertex2f;
import pisi.unitedmeows.yystal.utils.kThread;

import javax.imageio.ImageIO;

public enum YTestStart {
	gaming; /* :D */

	private static final YLogger logger = YYStal.createLogger(YTestStart.class)
			.time(YLogger.Time.DAY_MONTH_YEAR_FULL)
			.colored(true);

	public static void main(final String[] args) throws Exception {

        YWindow ywindow = new YWindow("yystal", 640, 480);
        ywindow.resizable.set(() -> true);
        ywindow.open();
        ywindow.exitEvent.bind(() -> System.out.println("exiting"));


        YLabel yLabel = new YLabel();
        yLabel.text.set("anananananan");

        yLabel.location(new Vertex2f(5, 40));
        ywindow.addElement(yLabel);

        YButton yButton = new YButton();
        yButton.text.set("Next image!");
        yButton.location(new Vertex2f(100, 300));
        ywindow.addElement(yButton);
        yButton.autoSize.set(true);

        yButton.clickEvent.bind(new ButtonClickEvent() {
            @Override
            public void onButtonClick() {
                try {
                    ywindow.background.set(new YBackgroundImage(ImageIO.read(new URL("https://picsum.photos/600"))));
                } catch (IOException e) {

                }
            }

        });

        while (!ywindow.isClosed()) {
            kThread.sleep(1000);
        }
	}

}
