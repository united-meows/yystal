package pisi.unitedmeows.yystal.clazz;

import pisi.unitedmeows.yystal.exception.YExManager;
import pisi.unitedmeows.yystal.exception.impl.YexNullPtr;
import pisi.unitedmeows.yystal.utils.Pair;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class event<X extends delegate> {
	
	protected List<Pair<delegate, Method>> delegateList;

	public event() {
		delegateList = new ArrayList<>(1);
	}

	public void fire(Object... params) {
		for (Pair<delegate, Method> binded : delegateList) {
			try {
				binded.item2().invoke(binded.item1(), params);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void bind(X del) {
		try {
			Method method = del.getClass().getDeclaredMethods()[0];
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			delegateList.add(new Pair<>(del, method));
		} catch (NullPointerException ex) {
			YExManager.pop(new YexNullPtr("Delegate method couldn't be found " + del.getClass()));
		}
	}
}
