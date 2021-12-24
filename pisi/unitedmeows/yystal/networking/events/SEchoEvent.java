package pisi.unitedmeows.yystal.networking.events;

import pisi.unitedmeows.yystal.clazz.delegate;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;

public interface SEchoEvent extends delegate {
	public void onDataReceived(YSocketClient socketClient, byte[] data, ref<Boolean> canceled);
}
