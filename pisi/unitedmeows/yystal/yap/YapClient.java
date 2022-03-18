package pisi.unitedmeows.yystal.yap;

import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.events.CDataReceivedEvent;
import pisi.unitedmeows.yystal.utils.MemoryReader;
import pisi.unitedmeows.yystal.yap.events.YCSignalReceived;

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
					signalReceivedEvent.fire(new YapSignal(memoryReader));
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

	public YapSignalBuilder newSignal() {
		return YapSignalBuilder.builder();
	}

	public void send(YapSignal signal) {
		tcpClient.send(signal.reader().getBytes());
	}

	public void send(YapSignalBuilder builder) {
		tcpClient.send(builder.buildBytes());
	}

	public YapClient() {
		this(new YTcpClient().makeFixed());
	}


}
