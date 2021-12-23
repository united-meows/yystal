package pisi.unitedmeows.yystal.parallel;

public interface ITaskPool {
	Task run(IFunction<?> function, Future<?> future);
	Task run_w(IFunction<?> function, Future<?> future, long after);
	int workerCount();
	Task nextTask();
	void register();
	void unregister();
	TaskWorker getWorker();
	TaskWorker getWorker(Task task);
	void stopWorker(TaskWorker worker);
	void stopWorker(TaskWorker worker, boolean abort);
	void newWorker();
}
