package pisi.unitedmeows.yystal.exception;

import java.util.function.Consumer;

public class YTry {

    public static YBasicTry create(YExceptionRunnable _runnable) {
        final YBasicTry basicTry = new YBasicTry(_runnable);
        return basicTry;
    }
}
