package pisi.unitedmeows.yystal.networking.events;

import pisi.unitedmeows.yystal.clazz.delegate;

@FunctionalInterface
public interface CDataReceivedEvent extends delegate {
	public void onDataReceived(byte[] data);
}
