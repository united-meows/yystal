package pisi.unitedmeows.yystal.exception;

import java.util.function.Consumer;

public class YexTry {

    private Runnable runnable;
    private Consumer<YEx> catcher;
    private Runnable end;

    public YexTry(Runnable _runnable) {
        runnable = _runnable;
    }

    public YexTry catcher(Consumer<YEx> _catcher) {
        catcher = _catcher;
        return this;
    }

    public YexTry end(Runnable _end) {
        end = _end;
        return this;
    }

    public void run() {
        YExManager.startCaching();
        runnable.run();

        YEx exception = YExManager.lastEx();
        if (exception != null)
            catcher.accept(exception);

        end.run();
    }
}
