package pisi.unitedmeows.yystal.parallel;

import pisi.unitedmeows.yystal.parallel.impl.ITaskPool;
import pisi.unitedmeows.yystal.utils.Stopwatch;

public class TaskWorker extends Thread
{
    private boolean running;
    private ITaskPool owner;
    private boolean busy;
    private Stopwatch stopwatch;

    public TaskWorker(ITaskPool owner) {
        this.owner = owner;
        stopwatch = new Stopwatch();
    }

    @Override
    public void run() {
        stopwatch.reset();
        running = true;
        while (running) {
            final Task task = owner.poll();

            if (task != null) {
                busy = true;
                task.execute();
                busy = false;
                stopwatch.reset();
            }
        }
    }

    public boolean isBusy() {
        return busy && stopwatch.isReached(/*TODO: TASKWORKER_BUSY_TIME */1000);
    }

    public TaskWorker setRunning(boolean running) {
        this.running = running;
        return this;
    }

    public TaskWorker stopWorker() {
        running = false;
        return this;
    }


    public boolean isRunning() {
        return running;
    }
}
