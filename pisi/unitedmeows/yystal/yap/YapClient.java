package pisi.unitedmeows.yystal.yap;

import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YTcpClient;
import pisi.unitedmeows.yystal.networking.events.CDataReceivedEvent;
import pisi.unitedmeows.yystal.utils.MemoryReader;
import pisi.unitedmeows.yystal.yap.events.YCSignalReceived;

public class YapClient {

	private YTcpClient tcpClient;
	public event<YCSignalReceived> signalReceivedEvent = new event<>();

	public YapClient(YTcpClient client) {
		tcpClient = client;
		tcpClient.dataReceivedEvent.bind(new CDataReceivedEvent() {
			@Override
			public void onDataReceived(byte[] data) {
				signalReceivedEvent.fire(new YapSignal(new MemoryReader(data)));
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
		System.out.println("writing data");
		tcpClient.send(signal.reader().getBytes());
	}

	public void send(YapSignalBuilder builder) {
		System.out.println("writing data");
		tcpClient.send(builder.buildBytes());
	}

	public YapClient() {
		this(new YTcpClient().makeFixed());
	}


}
