package pisi.unitedmeows.yystal.networking.events;

import pisi.unitedmeows.yystal.clazz.delegate;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;

public interface SDataReceivedEvent extends delegate {
	public void onClientDataReceived(YSocketClient client, byte[] data);
}
