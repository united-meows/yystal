package pisi.unitedmeows.yystal.parallel;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.prop;

public class Async {

	public static <X> Future<X> async(IAction action) {
		final Future<X> future = new Future<>();
		YYStal.taskPool().queue(new Task(future, action));
		return future;
	}

	public static <X> Future<X> async_w(IAction action, long time) {
		final Future<X> future = new Future<>();
		YYStal.taskPool().queue_w(new Task(future, action), time);
		return future;
	}

	public static <X> X await(Future<X> future) {
		return (X) future.await();
	}

	public static <X> X await(IAction action) {
		Future<X> future = async(action);
		return future.await();
	}

	public static <X> Future<X> when(IState state, IAction action, long interval) {
		prop<Promise> promiseProp = new prop<Promise>();
		prop<Future<X>> futureprop = new prop<>();
		promiseProp.set(async_loop((f) -> {
			if (state.check()) {
				futureprop.set(async(action));
				promiseProp.get().stop();
			}
		}, interval));
		return futureprop.get();
	}

	public static <X> Future<X> when(IState state, IAction action) {
		prop<Promise> promiseProp = new prop<Promise>();
		prop<Future<X>> futureprop = new prop<>();
		promiseProp.set(async_loop((f) -> {
			if (state.check()) {
				futureprop.set(async(action));
				promiseProp.get().stop();
			}
		}, 10L));
		return futureprop.get();
	}




	public static Promise async_loop(final IAction action, long interval) {
		final Future<?> future = new Future<>();
		final Promise promise = new Promise();
		prop<IAction> prop = new prop<IAction>();

		IAction loopAction = (f) -> {
			action.execute(future);
			if (promise.isValid()) {
				async_w(prop.get(), interval);
			}
		};


		prop.set(loopAction);
		async(loopAction);

		return promise;
	}

	public static Promise async_loop_w(final IAction action, long interval, long runAfter) {
		final Future<?> future = new Future<>();
		final Promise promise = new Promise();
		prop<IAction> prop = new prop<IAction>();

		IAction loopAction = (f) -> {
			action.execute(future);
			if (promise.isValid()) {
				async_w(prop.get(), interval);
			}
		};

		prop.set(loopAction);
		async_w(loopAction,  runAfter);
		return promise;
	}

	public static Promise async_loop_times(final IAction action, long interval, int times) {
		final Future<?> future = new Future<>();
		final Promise promise = new Promise();
		prop<IAction> prop = new prop<IAction>();

		prop<Integer> timesProp = new prop<>(times);

		IAction loopAction = (f) -> {
			action.execute(future);
			if (promise.isValid() && timesProp.get() > 0) {
				async_w(prop.get(), interval);
				timesProp.set(timesProp.get() - 1);
			}
		};

		prop.set(loopAction);
		async(loopAction);
		return promise;
	}

	public static Promise async_loop_times_w(final IAction action, long interval, long runAfter, int times) {
		final Future<?> future = new Future<>();
		final Promise promise = new Promise();
		prop<IAction> prop = new prop<IAction>() {
			@Override
			public IAction get() {
				return super.get();
			}

			@Override
			public void set(IAction newValue) {
				super.set(newValue);
			}
		};

		prop<Integer> timesProp = new prop<Integer>(times) {
			@Override
			public Integer get() {
				return super.get();
			}

			@Override
			public void set(Integer newValue) {
				super.set(newValue);
			}
		};

		IAction loopAction = (f) -> {
			System.out.println(timesProp.get() == null);

			action.execute(future);
			if (promise.isValid() && timesProp.get() > 0) {
				async_w(prop.get(), interval);
				timesProp.set(timesProp.get() - 1);
			}
		};

		prop.set(loopAction);
		async_w(loopAction, runAfter);
		return promise;
	}
}
