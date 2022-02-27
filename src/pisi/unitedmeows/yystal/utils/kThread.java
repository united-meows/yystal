package pisi.unitedmeows.yystal.utils;

import pisi.unitedmeows.yystal.parallel.IState;

public class kThread {

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {

		}
	}

	public static void sleep_till(IState state) {
		while (state.check()) {
			sleep(1);
		}
	}

	public static void sleep_untill(IState state) {
		while (!state.check()) {
			sleep(1);
		}
	}
}
