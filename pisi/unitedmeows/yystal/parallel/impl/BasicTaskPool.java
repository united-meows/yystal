package pisi.unitedmeows.yystal.parallel.impl;

import pisi.unitedmeows.yystal.parallel.Task;
import pisi.unitedmeows.yystal.parallel.TaskWorker;
import pisi.unitedmeows.yystal.utils.Stopwatch;
import pisi.unitedmeows.yystal.utils.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingDeque;

public class BasicTaskPool implements ITaskPool {

    private LinkedBlockingDeque<Task> taskQueue;
    private Thread controlThread;
    private List<Vector2<Long, Task>> waitingTasks;
    private List<TaskWorker> taskWorkers;

    private boolean running;
    private int minWorkers, maxWorkers;

    private Stopwatch busyWatch;

    public BasicTaskPool(int minWorkers, int maxWorkers) {
        this.minWorkers = minWorkers;
        this.maxWorkers = maxWorkers;
        busyWatch = new Stopwatch();
        taskQueue = new LinkedBlockingDeque<>();
        waitingTasks = new ArrayList<>();
        taskWorkers = new ArrayList<>();
        controlThread = new Thread(this::control);

        for (int i = 0; i < minWorkers; i++) {
            final TaskWorker taskWorker = new TaskWorker(this);
            taskWorkers.add(taskWorker);
            taskWorker.start();
        }
    }

    public BasicTaskPool() {
        this(5, 15);
    }

    public void register() {
        running = true;
        controlThread.start();

    }


    public void unregister() {
        running = false;
    }

    private void control() {
        busyWatch.reset();
        while (running) {

            final long currentTime = System.currentTimeMillis();
            for (int i = 0; i < waitingTasks.size(); i++) {
                Vector2<Long, Task> waitingTask = waitingTasks.get(i);
                if (currentTime >= waitingTask.first()) {
                    queue(waitingTask.second());
                    waitingTasks.remove(i);
                    i--;
                }
            }

            if (workerCount() < maxWorkers) {
                int busyWorkers = 0;
                for (TaskWorker taskWorker : taskWorkers) {
                    if (taskWorker.isBusy()) {
                        busyWorkers++;
                    }
                }
                if (busyWorkers > (workerCount() / 3)) {
                    //TODO: REMOVE
                    final TaskWorker taskWorker = new TaskWorker(this);
                    taskWorkers.add(taskWorker);
                    taskWorker.start();
                }
                else if (workerCount() < maxWorkers && taskQueue.size() > (taskWorkers.size() - busyWorkers) * 10) {
                    final TaskWorker taskWorker = new TaskWorker(this);
                    taskWorkers.add(taskWorker);
                    taskWorker.start();
                } else if (workerCount() > minWorkers && busyWatch.isReached(3000)) {
                    taskWorkers.get(0).stopWorker();
                    taskWorkers.remove(0);
                    busyWatch.reset();
                }
            }
            

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }
        }
    }


    @Override
    public void queue(Task task) {
        taskQueue.add(task);
    }

    @Override
    public void queue_w(Task task, long time) {
        waitingTasks.add(new Vector2<Long, Task>(System.currentTimeMillis() + time, task));
    }

    @Override
    public int workerCount() {
        return taskWorkers.size();
    }

    @Override
    public Task poll() {
        while (taskQueue.isEmpty()) {
            try {
                Thread.sleep(4);
            } catch (InterruptedException e) {

            }
        }
        return taskQueue.poll();
    }
}
