package pisi.unitedmeows.yystal.parallel.repeaters;

import pisi.unitedmeows.yystal.YYStal;

/**
 repeater is better to use than YTimer when you have multiple repeaters with same
 interval. Because all same interval repeaters gets called in same TaskWorker
 **/
public class Repeater {

    private boolean alive;
    private Runnable tick;
    private final int interval;


    @Deprecated public Repeater(final int _interval) { interval = _interval; }

    public Repeater onTick(Runnable _tick) {
        tick = _tick;
        return this;
    }


    public Repeater alive(boolean _alive) {
        alive = _alive;
        return this;
    }

    @Deprecated public void _tick() {
        tick.run();
    }

    public boolean isAlive() {
        return alive;
    }

    public Repeater pause() {
        alive = false;
        YYStal.repeaterPool().unregisterRepeater(interval(), this);
        return this;
    }

    public Repeater resume() {
        alive = true;
        YYStal.repeaterPool().registerRepeater(interval(), this);
        return this;
    }

    public int interval() {
        return interval;
    }
}
