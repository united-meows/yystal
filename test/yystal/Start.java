package test.yystal;


import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.basic.YTcpEchoServer;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.events.CDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.events.SDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;
import pisi.unitedmeows.yystal.parallel.Future;
import pisi.unitedmeows.yystal.utils.CoID;
import pisi.unitedmeows.yystal.utils.Stopwatch;
import pisi.unitedmeows.yystal.utils.kThread;
import pisi.unitedmeows.yystal.web.YWebClient;
import pisi.unitedmeows.yystal.yap.YAppServer;
import pisi.unitedmeows.yystal.yap.YapConstants;

import static pisi.unitedmeows.yystal.YYStal.*;
import static pisi.unitedmeows.yystal.parallel.Async.*;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class Start {

	public static void main(String[] args) {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.reset();
		for (int i = 1000; i > 0; i--) {
			System.out.println(CoID.generate().toString());
		}
		System.out.println(stopwatch.elapsed());


		kThread.sleep(100000);

	}

}
