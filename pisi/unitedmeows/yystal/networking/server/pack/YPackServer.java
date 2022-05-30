package pisi.unitedmeows.yystal.networking.server.pack;


import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.events.SDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;
import pisi.unitedmeows.yystal.utils.MemoryReader;
import pisi.unitedmeows.yystal.networking.server.pack.events.YSSignalReceived;

public class YPackServer {

    protected YTcpServer tcpServer;

    public event<YSSignalReceived> signalReceivedEvent = new event<>();

    public YPackServer(final IPAddress address, final int port) {
        tcpServer = new YTcpServer(address, port);
    }

    public YPackServer(final int port) {
        this(IPAddress.LOOPBACK, port);
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
                signalReceivedEvent.fire(client, new YSignal(new MemoryReader(data)));
            }
        });
    }

    public YTcpServer tcpServer() {
        return tcpServer;
    }
}