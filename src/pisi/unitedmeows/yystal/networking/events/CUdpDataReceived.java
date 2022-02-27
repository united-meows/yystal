package pisi.unitedmeows.yystal.networking.events;

import pisi.unitedmeows.yystal.clazz.delegate;

public interface CUdpDataReceived extends delegate {
	public void onDataReceived(byte[] data);
}
