package pisi.unitedmeows.yystal.yap.events;

import pisi.unitedmeows.yystal.clazz.delegate;
import pisi.unitedmeows.yystal.yap.YapSignal;

public interface YCSignalReceived extends delegate {
	public void onDataReceived(YapSignal signal);
}
