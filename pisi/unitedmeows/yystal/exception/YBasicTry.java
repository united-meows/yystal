package pisi.unitedmeows.yystal.exception;

import java.util.function.Consumer;

public class YBasicTry {

    private YExceptionRunnable runnable;
    private Consumer<Exception> catcher;
    private Runnable end;

    public YBasicTry(YExceptionRunnable _runnable) {
        runnable = _runnable;
    }

    public YBasicTry catcher(Consumer<Exception> _catcher) {
        catcher = _catcher;
        return this;
    }

    public YBasicTry end(Runnable _end) {
        end = _end;
        return this;
    }

    public void run() {
        try {
            runnable.run();
        } catch (Exception ex) {
            if (catcher != null)
                catcher.accept(ex);
        } finally {
            if (end != null)
                end.run();
        }
    }
}
