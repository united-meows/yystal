package test.yystal;

import static pisi.unitedmeows.yystal.YYStal.startWatcher;
import static pisi.unitedmeows.yystal.YYStal.stopWatcher;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.fusesource.jansi.AnsiConsole;
import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.logger.impl.YLogger;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.events.CDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.events.SDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.YTcpPool;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;
import pisi.unitedmeows.yystal.networking.server.extension.impl.STcpLineRead;
import pisi.unitedmeows.yystal.ui.YWindow;
import pisi.unitedmeows.yystal.utils.YRandom;
import pisi.unitedmeows.yystal.utils.kThread;

public enum YTestStart {
	gaming; /* :D */
	private static final YLogger logger = YYStal.createLogger(YTestStart.class).setTime(YLogger.Time.DAY_MONTH_YEAR_FULL).setColored(true);

	public static void main(final String[] args) {



	}
}
