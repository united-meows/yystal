package pisi.unitedmeows.yystal.exception;

import java.util.function.Consumer;

public class YTry {

    public static YBasicTry basicTry(YExceptionRunnable _runnable) {
        return  new YBasicTry(_runnable);
    }

    public static YexTry create(Runnable _runnable) {
        return new YexTry(_runnable);
    }
}
