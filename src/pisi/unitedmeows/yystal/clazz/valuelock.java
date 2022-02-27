package pisi.unitedmeows.yystal.clazz;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.utils.kThread;

/* locks the current thread until it get freed by another thread by getting a value */
public class valuelock<X> extends HookClass<X> {

	private boolean locked;

	public void __setup() {
		locked = true;
		kThread.sleep_till(() ->  locked);
	}

	public void free(X _value) {
		hooked = _value;
		locked = false;
	}

	public X get() {
		return hooked;
	}
}
