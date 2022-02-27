package pisi.unitedmeows.yystal.clazz;

import pisi.unitedmeows.yystal.utils.YRandom;

import java.util.HashMap;

public class shared<X>{
	private final HashMap<Byte, X> values = new HashMap<>();

	public X get(byte data) {
		return values.getOrDefault(data, null);
	}

	public byte put(X obj) {
		byte identifier;
		do {
			identifier = YRandom.BASIC.nextByte();
		} while (values.containsKey(identifier));

		values.put(identifier, obj);
		return identifier;
	}

	public void remove(byte identifier) {
		values.remove(identifier);
	}
}
