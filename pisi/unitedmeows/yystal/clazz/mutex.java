package pisi.unitedmeows.yystal.utils;

public class Mutex {

	private boolean locked;

	public void lock() {
		locked = true;
		kThread.sleep_till(() -> { return locked; });
	}

	public void unlock() {
		locked = false;
	}

}
