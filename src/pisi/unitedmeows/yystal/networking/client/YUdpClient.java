package pisi.unitedmeows.yystal.networking.client;

import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.exception.YExManager;
import pisi.unitedmeows.yystal.exception.impl.YexIO;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.events.CUdpDataReceived;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class YUdpClient implements Closeable {

	protected IPAddress connectedHost;
	protected int connectedPort;
	protected DatagramSocket socket;
	private Thread receiveThread;
	private byte[] buffer;

	public event<CUdpDataReceived> onDataReceived = new event<CUdpDataReceived>();

	public YUdpClient() {
		this(512);
	}

	public YUdpClient(int bufferSize) {
		buffer = new byte[bufferSize];
	}

	public boolean connect(IPAddress _address, int _port) {
		if (socket != null) {
			socket.close();
		}
		try {
			socket = new DatagramSocket();
		} catch (Exception ex) {
			YExManager.pop(new YexIO(String.format("Couldn't connect to the udp server %s", ex.getMessage())));
			return false;
		}

		connectedHost = _address;
		connectedPort = _port;

		if (receiveThread != null) {
			try {
				receiveThread.stop();
			} catch (Exception ex) {
				YExManager.pop(new YexIO(String.format("Couldn't stop last receive thread %s", ex.getMessage())));
			}
		}

		receiveThread = new Thread(this::receive);
		receiveThread.start();
		return true;
	}

	public void receive() {
		while (isConnected()) {
			DatagramPacket response = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(response);
				byte[] received = Arrays.copyOfRange(buffer, 0, response.getLength());
				onDataReceived.fire(received);
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

	public void send(byte[] data) {
		DatagramPacket request = new DatagramPacket(data, data.length, connectedHost.inetAddress.get(), connectedPort);
		try {
			socket.send(request);
		} catch (IOException ex) {
			YExManager.pop(new YexIO(String.format("Couldn't send the data %s", ex.getMessage())));
		}
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

	public boolean isConnected() {
		return !socket.isClosed();
	}
}
