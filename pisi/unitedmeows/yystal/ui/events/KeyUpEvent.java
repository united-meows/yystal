package pisi.unitedmeows.yystal.ui.events;

import pisi.unitedmeows.yystal.clazz.delegate;

@FunctionalInterface
public interface KeyUpEvent extends delegate {
    void onKeyDownEvent(int keyCode);
}
