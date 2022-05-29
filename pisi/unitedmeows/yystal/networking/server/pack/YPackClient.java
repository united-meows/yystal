package pisi.unitedmeows.yystal.networking.server.yap;

import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.events.CDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.server.pack.YSignal;
import pisi.unitedmeows.yystal.networking.server.pack.YSignalBuilder;
import pisi.unitedmeows.yystal.utils.MemoryReader;
import pisi.unitedmeows.yystal.networking.server.yap.events.YCSignalReceived;

import java.io.IOException;

public class YapClient {

	private YTcpClient tcpClient;
	public event<YCSignalReceived> signalReceivedEvent = new event<>();

	public YapClient(YTcpClient client) {
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

	public YapClient() {
		this(new YTcpClient().makeFixed());
	}


}
