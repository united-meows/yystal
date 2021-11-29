package pisi.unitedmeows.yystal.parallel.impl;

import pisi.unitedmeows.yystal.parallel.Task;

public interface ITaskPool {
	Task poll();
	void queue(Task task);
	void queue_w(Task task, long time);
	int workerCount();
	void register();
	void unregister();
}
