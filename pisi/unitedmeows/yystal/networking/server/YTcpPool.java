package pisi.unitedmeows.yystal.networking.server;

import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.events.SDataReceivedEvent;

import java.net.ServerSocket;

public class YTcpPool extends YTcpServer {

    public YTcpPool(IPAddress _listenIp, int _listenPort) {
        super(_listenIp, _listenPort);
    }

    public YTcpPool(ServerSocket _serverSocket, IPAddress _listenIp, int _listenPort) {
        super(_serverSocket, _listenIp, _listenPort);
    }

    @Override
    public boolean listen() {
        boolean result = super.listen();

        if (result) {
            dataReceiveEvent.bind(new SDataReceivedEvent() {
                @Override
                public void onClientDataReceived(YSocketClient client, byte[] data) {
                    for (YSocketClient otherSocket : connectedClients()) {
                        if (otherSocket != client) {
                            otherSocket.write(data);
                        }
                    }
                }
            });
        }

        return result;
    }

    @Override
    public YTcpPool makeFixed() {
        return (YTcpPool) super.makeFixed();
    }
}
