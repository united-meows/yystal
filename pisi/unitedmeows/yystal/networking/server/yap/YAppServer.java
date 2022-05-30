package pisi.unitedmeows.yystal.networking.server.yap;

import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.events.SDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;
import pisi.unitedmeows.yystal.networking.server.pack.YPackServer;
import pisi.unitedmeows.yystal.networking.server.pack.YSignal;
import pisi.unitedmeows.yystal.networking.server.pack.YSignalBuilder;
import pisi.unitedmeows.yystal.utils.CoID;
import pisi.unitedmeows.yystal.utils.MemoryReader;
import pisi.unitedmeows.yystal.networking.server.pack.events.YSSignalReceived;
import stelix.xfile.SxfBlockBuilder;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class YAppServer extends YPackServer {

	protected YTcpServer tcpServer;
	private String appName;
	private double appVersion;
	private CoID appId;
	public final String introductionMessage;
	public event<YSSignalReceived> signalReceivedEvent = new event<>();

	public YAppServer(String _appName, double _appVersion, CoID _appId, final IPAddress address, final int port) {
		super(address, port);
		appName = _appName;
		appVersion = _appVersion;
		appId = _appId;
		introductionMessage = createAppIntroduction();
	}

	public YAppServer(String _appName, double _appVersion, CoID _appId, final int port) {
		this(_appName, _appVersion, _appId, IPAddress.LOOPBACK, port);
	}

	public YAppServer(String _appName, double _appVersion, CoID _appId) {
		this(_appName, _appVersion, _appId, findAvailablePort());
	}

	public int port() {
		return tcpServer.port();
	}

	public YSignalBuilder newSignal() {
		return YSignalBuilder.builder();
	}

	@Override
	public void listen() {
		tcpServer.makeFixed();
		tcpServer.listen();

		tcpServer.dataReceiveEvent.bind(new SDataReceivedEvent() {
			@Override
			public void onClientDataReceived(YSocketClient client, byte[] data) {
				if (Arrays.equals(data, YapConstants.ASK_INTRODUCTION.data())) {
					client.write(introductionMessage.getBytes(StandardCharsets.UTF_8));
				} else {
					signalReceivedEvent.fire(client, new YSignal(new MemoryReader(data)));
				}
			}
		});
	}

	protected static int findAvailablePort() {
		String osName = System.getProperty("os.name").toLowerCase();
		boolean isMacOs = osName.startsWith("mac os x");
		for (int port = 3217; port < 3217 + 30; port++) {
			if (isAvailablePort(port, isMacOs)) {
				return port;
			}
		}
		return -1;
	}

	protected String createAppIntroduction() {
		return SxfBlockBuilder.create()
				.add("name", appName).build()
				.add("version", appVersion).build()
				.add("id", String.valueOf(appId.toString())).build().buildBlock().toString();
	}

	protected static boolean isAvailablePort(int port, boolean isMac) {
		try (ServerSocket serverSocket = new ServerSocket()) {
			if (isMac) {
				serverSocket.setReuseAddress(false);
			}
			serverSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port), 1);
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
	public YTcpServer tcpServer() {
		return tcpServer;
	}
}
