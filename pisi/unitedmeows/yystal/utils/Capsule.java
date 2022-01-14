package pisi.unitedmeows.yystal.utils;

import pisi.unitedmeows.yystal.clazz.function;
import pisi.unitedmeows.yystal.parallel.IFunction;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Capsule {

	protected final HashMap<String, Object> parameters;

	public static Capsule of(Pair<String, ?>... params) {
		final Capsule capsule = new Capsule(params.length);
		for (Pair<String, ?> param : params) {
			capsule.parameters.put(param.item1(), param.item2());
		}
		return capsule;
	}

	protected Capsule(int paramCount) {
		parameters = new HashMap<>(paramCount);
	}

	public <X> X get(String paramName) {
		return (X) parameters.getOrDefault(paramName, null);
	}

	public <X> X getOrDefault(String paramName, X defaultVal) {
		return (X) parameters.getOrDefault(paramName, defaultVal);
	}

	public boolean contains(String paramName) {
		return parameters.containsKey(paramName);
	}

	public boolean exists() {
		return !parameters.isEmpty();
	}

	public <X> X ifExists(String paramName, Consumer<X> func) {
		X value = get(paramName);
		if (value != null) {
			func.accept(value);
		}
		return value;
	}

}
