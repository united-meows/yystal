package pisi.unitedmeows.yystal.yap.events;

import pisi.unitedmeows.yystal.clazz.delegate;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.yap.YapSignal;

public interface YSSignalReceived extends delegate {
	public void onSignalReceived(YSocketClient socketClient, YapSignal signal);
}
