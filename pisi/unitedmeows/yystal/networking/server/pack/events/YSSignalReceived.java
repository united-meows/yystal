package pisi.unitedmeows.yystal.networking.server.yap.events;

import pisi.unitedmeows.yystal.clazz.delegate;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.pack.YSignal;

public interface YSSignalReceived extends delegate {
	public void onSignalReceived(YSocketClient socketClient, YSignal signal);
}
