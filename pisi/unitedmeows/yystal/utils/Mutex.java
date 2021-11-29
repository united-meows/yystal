package pisi.unitedmeows.yystal.utils;

public class Mutex {

	private boolean locked;

	public void lock() {
		kThread.sleep_till(() -> { return locked; });
		locked = true;
	}

	public void unlock() {
		locked = false;
	}

}
