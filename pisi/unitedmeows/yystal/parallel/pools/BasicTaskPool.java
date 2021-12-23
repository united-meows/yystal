package pisi.unitedmeows.yystal.parallel;

import pisi.unitedmeows.yystal.YSettings;
import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.utils.kThread;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class BasicTaskPool implements ITaskPool
{
	/* taskworkers that runs the tasks */
	private List<TaskWorker> taskWorkers;

	/* controller thread that controls worker */
	private Thread controlThread;

	/* maximum and the minimum number of workers in a pool */
	private int minWorkers, maxWorkers;

	/* tasks in the queue */
	private ArrayBlockingQueue<Task> taskQueue;

	public BasicTaskPool(int minWorkers, int maxWorkers) {
		this.minWorkers = minWorkers;
		this.maxWorkers = maxWorkers;
		taskQueue = new ArrayBlockingQueue<Task>();
	}

	/* gets called when the client starts using this pool */
	@Override
	public void register() {
		controlThread = new Thread(new Runnable() {
			@Override
			public void run() {
				control();
			}
		});

		for (int i = 0; i < minWorkers; i++) {
			final TaskWorker taskWorker = new TaskWorker();
			taskWorkers.add(taskWorker);
			taskWorker.start();
		}

		controlThread.start();
	}

	/* gets called when client switches the pool for another pool */
	@Override
	public void unregister() {
		for (TaskWorker taskWorker : taskWorkers) {
			taskWorker.stopWorker();
		}
		taskWorkers.clear();

		/* maybe add unfinished tasks to new pool? */
		taskQueue.clear();
	}

	/* controller thread */
	public void control() {
		while (true) {

			boolean allBusy = true;
			for (TaskWorker taskWorker : taskWorkers) {
				if (!taskWorker.isBusy()) {
					allBusy = false;
					break;
				}
			}

			/* if all workers are busy, adds new workers on the way */
			if (allBusy && workerCount() < maxWorkers) {
				final TaskWorker taskWorker = new TaskWorker();
				taskWorkers.add(taskWorker);
				taskWorker.start();
			}

			/* if a worker is free for some time removes the worker */
			if (!allBusy && workerCount() > minWorkers) {
				long time = System.currentTimeMillis() - 500  /* if worker is free more than 500ms */;
				Iterator<TaskWorker> taskWorkerIterator = taskWorkers.iterator();
				while (taskWorkerIterator.hasNext()) {
					if (workerCount() > minWorkers) {
						final TaskWorker taskWorker = taskWorkerIterator.next();
						if (taskWorker.lastTaskFinish() < time) {
							taskWorkerIterator.remove();
						}
					}
				}
			}

			/* waits  */
			kThread.sleep(YYStal.setting(YSettings.TASKPOOL_CONTROL_CHECK_DELAY));
		}
	}

	/* runs the function async */
	@Override
	public void run(IFunction function) {
		taskQueue.add(new Task(function));
	}

	@Override
	public int workerCount() {
		return taskWorkers.size();
	}

	@Override
	public Task nextTask() {
		return null;
	}
}
