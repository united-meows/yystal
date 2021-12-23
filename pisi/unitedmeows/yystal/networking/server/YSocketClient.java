package pisi.unitedmeows.yystal.networking.server;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.out;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.exception.YExManager;
import pisi.unitedmeows.yystal.exception.impl.YexIO;
import pisi.unitedmeows.yystal.networking.server.extension.STcpExtension;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;

public class YSocketClient {
	private static byte[] BUFFER = new byte[4096 * 2];
	private final Socket socket;
	private YTcpServer connectedServer;
	private Thread receiveThread;

	private DataInputStream inputStream;
	private DataOutputStream outputStream;

	public YSocketClient(Socket _socket, YTcpServer _connectedServer) {
		socket = _socket;
		connectedServer = _connectedServer;

		try {
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
			socket.setTcpNoDelay(true);
			socket.setReuseAddress(true);
		} catch (IOException ex) {
			YExManager.pop(new YexIO(String.format("Couldn't set attributes of connected client %s", ex.getMessage())));
		}
		receiveThread = new Thread(this::receive);
		receiveThread.start();
	}

	public void write(byte[] data) {
		try {
			ref<byte[]> sendData = YYStal.reference(data);
			ref<Boolean> shouldSend = YYStal.reference(true);
			connectedServer().extensions().forEach(x-> {
				x.onDataSend(this, sendData, shouldSend);
			});
			if (shouldSend.get()) {
				outputStream.write(sendData.get());
				outputStream.flush();
			}
		} catch (IOException ex) {
			YExManager.pop(new YexIO(String.format("Couldn't send data to client %s", ex.getMessage())));
		}
	}

	public void close() {
		try {

			socket.close();
			outputStream.close();
			inputStream.close();
			Iterator<YSocketClient> socketClientIterator = connectedServer().connectedClients().iterator();
			while (socketClientIterator.hasNext()) {
				if (socketClientIterator.next() == this) {
					socketClientIterator.remove();
					break;
				}
			}
			connectedServer().extensions().forEach(x -> {
				x.onClientDisconnect(this);
			});

		} catch (IOException ex) {
			YExManager.pop(new YexIO(String.format("Error while closing the client's socket %s", ex.getMessage())));
		}
	}

	public YTcpServer connectedServer() {
		return connectedServer;
	}

	protected void receive() {
		while (socket.isConnected()) {
			try {
				ref<Boolean> cancelDefaultReader = YYStal.reference(false);
				out<byte[]> receiveData = YYStal.out();
				connectedServer().extensions().forEach(extension -> {
					extension.onPreDataReceive(this, inputStream, cancelDefaultReader, receiveData);
				});
				byte[] data;
				if (cancelDefaultReader.get()) {
					data = receiveData.get();
				} else {
					int size = inputStream.read(BUFFER);
					data = Arrays.copyOf(BUFFER, size);
				}
				ref<byte[]> refData = YYStal.reference(data);
				connectedServer().extensions().forEach(extension -> {
					extension.onPostDataReceive(this, refData);
				});
				connectedServer().dataReceiveEvent.fire(this, refData.get());

			} catch (IOException ex) {
				YExManager.pop(new YexIO(String.format("Couldn't receive data from the client %s", ex.getMessage())));
			}
		}
	}

}