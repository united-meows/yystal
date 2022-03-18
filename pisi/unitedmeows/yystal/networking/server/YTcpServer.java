package pisi.unitedmeows.yystal.networking.server;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.exception.YExManager;
import pisi.unitedmeows.yystal.exception.impl.YexIO;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.events.SConnectionReceivedEvent;
import pisi.unitedmeows.yystal.networking.events.SDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.server.extension.STcpExtension;
import pisi.unitedmeows.yystal.networking.server.extension.impl.STcpFixedSize;
import pisi.unitedmeows.yystal.networking.server.extension.impl.STcpLineRead;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class YTcpServer {

	/* listening ip address */
	private IPAddress listening;

	/* listening port */
	private int port;

	/* connected clients */
	private List<YSocketClient> connectedClients;

	/* data receive event */
	public event<SDataReceivedEvent> dataReceiveEvent = new event<>();
	public event<SConnectionReceivedEvent> connectionReceivedEvent = new event<>();

	private Thread connectionThread;
	private ServerSocket serverSocket;
	private List<STcpExtension> extensions;


	public YTcpServer(IPAddress _listenIp, int _listenPort) {
		listening = _listenIp;
		port = _listenPort;
		extensions = new ArrayList<>();
	}

	public YTcpServer(ServerSocket _serverSocket, IPAddress _listenIp, int _listenPort) {
		this(_listenIp, _listenPort);
		serverSocket = _serverSocket;
	}

	public void registerExtension(STcpExtension extension) {
		extensions().add(extension);
		extension.setup(this);
	}


	public boolean listen() {
		if (serverSocket == null) {
			try {
				serverSocket = new ServerSocket(port, 50, listening.inetAddress.get());
			} catch (IOException e) {
				YExManager.pop(new YexIO(String.format("Couldn't start the server %s", e.getMessage())));
				return false;
			}
		}
		connectedClients = new ArrayList<>();
		connectionThread = new Thread(this::startListening);
		connectionThread.start();
		return true;
	}

	private void startListening() {
		while (!serverSocket.isClosed()) {
			try {
				Socket socket = serverSocket.accept();
				ref<Boolean> accepted = YYStal.reference(true);
				extensions.forEach(x-> {
					x.onConnectionRequest(socket, accepted);
				});

				if (accepted.get()) {
					YSocketClient ySocketClient = new YSocketClient(socket, this);
					connectedClients.add(ySocketClient);
					connectionReceivedEvent.fire(ySocketClient);
				} else {
					socket.close();
				}
			} catch (IOException ex) {
				YExManager.pop(new YexIO(String.format("Server couldn't accept the client %s", ex.getMessage())));
			}
		}
	}

	public void close() {
		try {
			kickAll();
			connectedClients.clear();
			serverSocket.close();
		} catch(IOException e) {

		}
	}

	public void kickAll() {
		for (YSocketClient client : connectedClients()) {
			client._preClose();
		}
	}

	public YTcpServer makeFixed() {
		extensions().add(new STcpFixedSize());
		return this;
	}

	public YTcpServer makeLineBased() {
		extensions.add(new STcpLineRead());
		return this;
	}

	public List<STcpExtension> extensions() {
		return extensions;
	}

	public List<YSocketClient> connectedClients() {
		return connectedClients;
	}

	public int port() {
		return port;
	}
}
