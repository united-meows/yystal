package pisi.unitedmeows.yystal.networking.client;

import pisi.unitedmeows.yystal.YSettings;
import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.clazz.mutex;
import pisi.unitedmeows.yystal.clazz.out;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.exception.YExManager;
import pisi.unitedmeows.yystal.exception.impl.YexIO;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.extension.CTcpExtension;
import pisi.unitedmeows.yystal.networking.client.extension.impl.CTcpFixedSize;
import pisi.unitedmeows.yystal.networking.events.CDataReceivedEvent;
import pisi.unitedmeows.yystal.utils.IDisposable;
import pisi.unitedmeows.yystal.utils.kThread;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class YTcpClient implements IDisposable {

	/* receives the data the server has sent */
	protected Thread receiveThread;
	/* writes the data that in the queue */
	protected Thread writeThread;

	/* send queue */
	protected Queue<byte[]> _sendQueue;

	/* raw java socket */
	protected Socket socket;

	/* connected server address */
	protected IPAddress connectedAddress;

	/* connected server port */
	protected int connectedPort;

	protected DataInputStream inputStream;
	protected DataOutputStream outputStream;

	private byte[] BUFFER = new byte[4096 * 2];
	protected List<CTcpExtension> extensions;


	public event<CDataReceivedEvent> dataReceivedEvent;

	public YTcpClient() {
		_sendQueue = new ConcurrentLinkedQueue<>();
		extensions = new ArrayList<>();
		dataReceivedEvent = new event<>();
	}

	/* connects to client */
	public boolean connect(IPAddress address, int port) {

		final ref<IPAddress> connectionAddress = YYStal.reference(address);
		final ref<Integer> connectionPort = YYStal.reference(port);
		final ref<Boolean> accepted = YYStal.reference(true);

		// applying the extensions
		extensions().forEach(x-> {
			x.onConnection(connectionAddress, connectionPort, accepted);
		});


		if (!accepted.get()) {
			return false;
		}

		address = connectionAddress.get();
		port = connectionPort.get();

		if (socket != null && socket.isConnected()) {
			try {
				/* maybe don't stop?? */
				socket.close();
				receiveThread.stop();
				writeThread.stop();
			} catch (IOException e) {

			}
		}
		try {
			socket = new Socket(address.getAddress(), port);
		} catch (IOException e) {
			YExManager.pop(new YexIO(String.format("Couldn't connect to server %s with port of %d " + System.lineSeparator() + " Raw Exception: %s", address.getAddress(), port, e.getMessage())));
			return false;
		}
		try {
			socket.setReuseAddress(true);
			socket.setTcpNoDelay(true);

			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());
		} catch (IOException ex) {
			YExManager.pop(new YexIO("Couldn't change socket attributes"));
		}




		if (socket.isConnected()) {
			receiveThread = new Thread(this::read);
			receiveThread.start();

			writeThread = new Thread(this::write);
			writeThread.start();

			connectedAddress = address;
			connectedPort = port;
			return true;
		} else {
			return false;
		}
	}

	protected void write() {
		while (isConnected()) {
			if (_sendQueue.isEmpty()) {
				kThread.sleep(YYStal.setting(YSettings.TCP_CLIENT_QUEUE_CHECK_DELAY));
			} else {
				byte[] data = _sendQueue.poll();
				try {
					ref<byte[]> modifiedData = YYStal.reference(data);
					ref<Boolean> shouldSend = YYStal.reference(true);

					extensions().forEach(extension -> {
						extension.onDataSend(modifiedData, shouldSend);
					});

					if (shouldSend.get()) {
						outputStream.write(modifiedData.get());
						outputStream.flush();
					}
				} catch (IOException e) {
					YExManager.pop(new YexIO(String.format("Unable to send data to server %s", e.getMessage())));
				}

				kThread.sleep(YYStal.setting(YSettings.TCP_CLIENT_WRITE_DELAY));
			}
		}
	}

	public YTcpClient makeFixed() {
		extensions().add(new CTcpFixedSize());
		return this;
	}


	public void send(byte[] data) {
		_sendQueue.add(data);
	}

    @Override
	public void close() {
		if (socket != null && socket.isConnected()) {
			ref<Boolean> canceled = YYStal.reference(false);
			extensions().forEach(extension -> {
				extension.onSocketClose(canceled);
			});
			extensions.clear();
			if (!canceled.get()) {
				_close();
			}
		}
        _sendQueue.clear();
        inputStream = null;
        outputStream = null;
        connectedAddress = null;
        BUFFER = null;
        dataReceivedEvent = null;
	}

	public byte[] readNext() {
		ref<byte[]> receivedData = YYStal.reference(null);
		final mutex waitLock = new mutex();

		int delegateId = dataReceivedEvent.bind(new CDataReceivedEvent() {
			@Override
			public void onDataReceived(byte[] data) {
				receivedData.set(data);
				waitLock.unlock();
			}
		});
		waitLock.lock();
		dataReceivedEvent.free(delegateId);
		return receivedData.get();
	}

	private boolean _close() {
		try {
			socket.close();
			receiveThread.stop();
			writeThread.stop();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}


	protected void read() {
		while (isConnected()) {
			try {
				ref<Boolean> cancelDefaultReader = YYStal.reference(false);
				out<byte[]> readData = YYStal.out();
				extensions.forEach(x -> {
					x.onPreDataReceive(inputStream, cancelDefaultReader, readData);
				});

				if (cancelDefaultReader.get()) {
					dataReceivedEvent.fire((Object) readData.get());
					ref<byte[]> modifiedData = YYStal.reference(readData.get());
					extensions.forEach(x -> {
						x.onPostDataReceive(modifiedData);
					});
				} else {
					int size = inputStream.read(BUFFER);
					if (size < 0) {
						close();
						return;
					}
					byte[] data = Arrays.copyOf(BUFFER, size);
					ref<byte[]> modifiedData = YYStal.reference(data);
					extensions.forEach(x -> {
						x.onPostDataReceive(modifiedData);
					});
					dataReceivedEvent.fire((Object) modifiedData.get());
				}
			} catch (IOException ex) {
				YExManager.pop(new YexIO(String.format("Unable to receive data from the server %s", ex.getMessage())));
			}
		}
	}

	public List<CTcpExtension> extensions() {
		return extensions;
	}

	public Socket socket() { return socket; }

	public void registerExtension(CTcpExtension extension) {
		extension.setup(this);
		extensions().add(extension);
	}

	public boolean isConnected() {
		return socket != null && !socket.isClosed() && socket.isConnected();
	}

}
