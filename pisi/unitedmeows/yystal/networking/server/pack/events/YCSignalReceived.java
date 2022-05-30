package pisi.unitedmeows.yystal.networking.server.pack.events;

import pisi.unitedmeows.yystal.clazz.delegate;
import pisi.unitedmeows.yystal.networking.server.pack.YSignal;

public interface YCSignalReceived extends delegate {
	public void onDataReceived(YSignal signal);
}
