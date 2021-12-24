package pisi.unitedmeows.yystal.yap;

import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.events.SDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;
import pisi.unitedmeows.yystal.utils.CoID;
import stelix.xfile.SxfDataBlock;
import stelix.xfile.SxfFile;
import stelix.xfile.writer.SxfWriter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class YAppServer {

	protected YTcpServer tcpServer;
	private String appName;
	private double appVersion;
	private CoID appId;
	private final String introductionMessage;

	public YAppServer(String _appName, double _appVersion, CoID _appId, final IPAddress address, final int port) {
		appName = _appName;
		appVersion = _appVersion;
		appId = _appId;
		introductionMessage = createAppIntroduction();
		tcpServer = new YTcpServer(address, port);
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


	public void listen() {
		tcpServer.makeFixed();
		tcpServer.listen();

		tcpServer.dataReceiveEvent.bind(new SDataReceivedEvent() {
			@Override
			public void onClientDataReceived(YSocketClient client, byte[] data) {
				if (Arrays.equals(data, YapConstants.ASK_INTRODUCTION.data())) {
					client.write(introductionMessage.getBytes(StandardCharsets.UTF_8));
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

	/*
		app => { name: 'Skuxx', version: 1.8, id:'$353tkşlrdgdfgdfg' }
	 */

	protected String createAppIntroduction() {
		SxfFile file = new SxfFile();
		SxfDataBlock app = new SxfDataBlock();
		app.putVar("name", appName);
		app.putVar("version", appVersion);
		app.putVar("id", appId.toString());
		file.put("app", app);
		SxfWriter sxfWriter = new SxfWriter();
		sxfWriter.setWriteType(SxfWriter.WriteType.INLINE);
		return sxfWriter.writeToString(file);
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
}
