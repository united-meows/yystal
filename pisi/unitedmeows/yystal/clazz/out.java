package pisi.unitedmeows.yystal.clazz;

/* the feature in c# */
/* allows you get value from method as a parameter */
public class out<X> extends HookClass<X> {

	public X get() {
		return hooked;
	}

	public void set(X value) {
		hooked = value;
	}
}
