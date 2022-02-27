package pisi.unitedmeows.yystal.clazz;

import pisi.unitedmeows.yystal.utils.kThread;

public class mutex {

	private boolean locked;

	public void lock() {
		locked = true;
		kThread.sleep_till(() -> { return locked; });
	}

	public void unlock() {
		locked = false;
	}

}
