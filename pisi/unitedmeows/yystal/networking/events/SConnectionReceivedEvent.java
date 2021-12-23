package pisi.unitedmeows.yystal.networking.events;

import pisi.unitedmeows.yystal.clazz.delegate;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;

public interface SConnectionReceivedEvent extends delegate {
	public void onClientConnected(YSocketClient socketClient);
}
