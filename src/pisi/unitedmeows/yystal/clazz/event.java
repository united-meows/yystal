package pisi.unitedmeows.yystal.clazz;

import pisi.unitedmeows.yystal.exception.YExManager;
import pisi.unitedmeows.yystal.exception.impl.YexNullPtr;
import pisi.unitedmeows.yystal.utils.Pair;
import pisi.unitedmeows.yystal.utils.YRandom;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class event<X extends delegate> {
	
	protected HashMap<Integer, Pair<delegate, Method>> delegateMap;

	public event() {
		delegateMap = new HashMap<>();
	}

	public void fire(Object... params) {
		for (Pair<delegate, Method> bound : delegateMap.values()) {
			try {

				bound.item2().invoke(bound.item1(), params);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void free(int delegateId) {
		delegateMap.remove(delegateId);
	}

	public int bind(X del) {
		int delegateId;

		do {
			delegateId = YRandom.BASIC.nextInRange(0, 217300);
		} while (delegateMap.containsKey(delegateId));

		try {


			Method method = del.getClass().getDeclaredMethods()[0];
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			delegateMap.put(delegateId ,new Pair<>(del, method));
			return delegateId;
		} catch (NullPointerException ex) {
			YExManager.pop(new YexNullPtr("Delegate method couldn't be found " + del.getClass()));
			return -1;
		}
	}
}
