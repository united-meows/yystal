package pisi.unitedmeows.yystal.networking.server.pack;

import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.events.CDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.server.pack.events.YCSignalReceived;
import pisi.unitedmeows.yystal.utils.MemoryReader;

import java.io.IOException;

public class YPackClient {

	private YTcpClient tcpClient;
	public event<YCSignalReceived> signalReceivedEvent = new event<>();

	public YPackClient(YTcpClient client) {
		tcpClient = client;
		tcpClient.dataReceivedEvent.bind(new CDataReceivedEvent() {
			@Override
			public void onDataReceived(byte[] data) {
				try (MemoryReader memoryReader = new MemoryReader(data)){
					signalReceivedEvent.fire(new YSignal(memoryReader));
				} catch (IOException ex) {}
			}
		});
	}

	public void connect(IPAddress address, int port) {
		tcpClient.connect(address, port);
	}


	public YTcpClient tcpClient() {
		return tcpClient;
	}

	public YSignalBuilder newSignal() {
		return YSignalBuilder.builder();
	}

	public void send(YSignal signal) {
		tcpClient.send(signal.reader().getBytes());
	}

	public void send(YSignalBuilder builder) {
		tcpClient.send(builder.buildBytes());
	}

	public YPackClient() {
		this(new YTcpClient().makeFixed());
	}


}
