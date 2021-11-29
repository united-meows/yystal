package pisi.unitedmeows.yystal.exception;

import pisi.unitedmeows.yystal.clazz.function;
import pisi.unitedmeows.yystal.parallel.IAction;

import java.util.HashMap;

public class YExManager {

	private static HashMap<Thread, YEx> lastExceptionMap = new HashMap<>();

	public static YEx lastEx() {
		return lastExceptionMap.getOrDefault(Thread.currentThread(), null);
	}

	public static void pop(YEx ex) {
		lastExceptionMap.put(Thread.currentThread(), ex);
	}

	public static void shot(function func, function pop) {
		YEx lastEx = lastEx();
		func.run();
		YEx current = lastEx();
		if (current != lastEx)
			pop.run();
	}
}
