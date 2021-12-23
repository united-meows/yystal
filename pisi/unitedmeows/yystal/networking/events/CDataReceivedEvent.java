package pisi.unitedmeows.yystal.networking.client.events;

import pisi.unitedmeows.yystal.clazz.delegate;

public interface CDataReceivedEvent extends delegate {
	public void onDataReceived(byte[] data);
}
