package pisi.unitedmeows.yystal.clazz;

/* similar like out but this time you have to initialize before sending as a parameter */
/* the feature in c# */
public class ref<X> extends HookClass<X> {

	public ref() {}

	public ref(X value) {
		set(value);
	}

	public X get() {
		return hooked;
	}

	public void set(X value) {
		hooked = value;
	}
}
