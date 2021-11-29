package pisi.unitedmeows.yystal.parallel;

public class Task {

	private Future<?> future;
	private IAction action;

	public Task(Future<?> future, IAction action) {
		this.future = future;
		this.action = action;
	}

	public void execute() {

		action.execute(future);
		future.markCompleted();
	}

}
