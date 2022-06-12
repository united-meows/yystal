package pisi.unitedmeows.yystal.ui.events;

import pisi.unitedmeows.yystal.clazz.delegate;

@FunctionalInterface
public interface WindowExitEvent extends delegate {
    void onWindowExit();
}
