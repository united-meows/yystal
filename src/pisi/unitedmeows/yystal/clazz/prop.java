package pisi.unitedmeows.yystal.clazz;

public class prop<X> {
	protected X value;

	public prop() {}
	public prop(X _value) { value = _value; }

	public void set(X newValue) { value = newValue;}
	public X get() { return value; }
}
