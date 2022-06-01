package pisi.unitedmeows.yystal.exception;

import pisi.unitedmeows.yystal.clazz.function;

import java.util.HashMap;

public class YExManager {

	private static HashMap<Thread, YEx> lastExceptionMap = new HashMap<>();

	public static <X> X lastEx() {
		return (X) lastExceptionMap.getOrDefault(Thread.currentThread(), null);
	}

    public static void startCaching() {
        lastExceptionMap.remove(Thread.currentThread());
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
