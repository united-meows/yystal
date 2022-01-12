package pisi.unitedmeows.yystal.utils;

import java.util.HashMap;

public class Types {

	protected static HashMap<Class<?>, Integer> typeLengths;

	static {
		typeLengths = new HashMap<Class<?>, Integer>() {{
			put(Integer.class, 4);
			put(Byte.class, 1);
			put(Long.class, 8);
			put(Short.class, 2);
			put(Boolean.class, 1);
			put(Double.class, 8);
			put(Character.class, 2);
		}};
	}

	public static int sizeof(Class<?> clazz) {
		return typeLengths.getOrDefault(clazz, 0);
	}
}
