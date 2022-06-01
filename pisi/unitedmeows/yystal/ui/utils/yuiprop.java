package pisi.unitedmeows.yystal.ui.utils;

import pisi.unitedmeows.yystal.clazz.prop;
import pisi.unitedmeows.yystal.ui.YWindow;

import java.util.function.Supplier;

public class yuiprop<X> {

    private YWindow window;
    protected X value;

    public yuiprop(YWindow _window) { window = _window;}
    public yuiprop(YWindow _window, Supplier<X> _value) { this(_window); set(_value); }

    public void set(Supplier<X> _value) {
        window.executeOnThread(() -> value = _value.get());
    }

    public X get() {
        return value;
    }
}
