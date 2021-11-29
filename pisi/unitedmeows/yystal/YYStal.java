package pisi.unitedmeows.yystal;

import com.sun.jndi.cosnaming.ExceptionMapper;
import pisi.unitedmeows.yystal.clazz.function;
import pisi.unitedmeows.yystal.exception.YEx;
import pisi.unitedmeows.yystal.exception.YExManager;
import pisi.unitedmeows.yystal.parallel.impl.BasicTaskPool;
import pisi.unitedmeows.yystal.parallel.impl.ITaskPool;

public class YYStal {

	private static ITaskPool currentPool;

	static {
		setCurrentPool(new BasicTaskPool());
	}

	public static void setCurrentPool(ITaskPool taskPool) {
		if (currentPool != null) {
			currentPool.unregister();
		}
		currentPool = taskPool;
		currentPool.register();
	}

	public static void pop(YEx ex) {
		YExManager.pop(ex);
	}

	public static void shot(function func, function pop) {
		YExManager.shot(func, pop);
	}

	public static YEx lastEx() {
		return YExManager.lastEx();
	}


	public static ITaskPool taskPool() {
		return currentPool;
	}

}
