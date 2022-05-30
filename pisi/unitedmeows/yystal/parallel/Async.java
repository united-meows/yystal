package pisi.unitedmeows.yystal.parallel;

import pisi.unitedmeows.yystal.YSettings;
import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.prop;
import pisi.unitedmeows.yystal.utils.kThread;

import java.util.function.Consumer;

public class Async {

    /* runs the function on taskpool without returning a value */
    public static <X> Future<X> async(IFunction<X> function) {
        Future<X> future = new Future<>();
        YYStal.taskPool().run(function, future);
        return future;
    }

    /* runs the function on task pool without returning a value */
    public static void async(Runnable function) {
        YYStal.taskPool().run(new IFunction<Integer>() {
            @Override
            public Integer run() {
                function.run();
                return 1;
            }
        }, null);
    }

	/* runs the task async and if the timeout has reached stops the task and returns the default value */
	public static <X> Future<X> async(IFunction<X> function, long timeout, IFunction<X> defaultValue) {
		final Future<X> future = new Future<>();
		Task task = YYStal.taskPool().run(new IFunction<X>() {
			@Override
			public X run() {
				return function.run();
			}
		}, future);

        async_w(() -> {
            if (!task.isExecuted()) {
                future.setResult(defaultValue.run());
                TaskWorker taskWorker = YYStal.taskPool().getWorker(task);
                if (taskWorker != null) {
                    YYStal.taskPool().stopWorker(taskWorker, true);
                    YYStal.taskPool().newWorker();
                }
            }
        }, timeout);
        return future;
    }

    /* runs the task async and if the timeout has reached stops the task and returns the default value */
    public static void async(Runnable runnable, long timeout) {
        Task task = YYStal.taskPool().run(new IFunction<Integer>() {
            @Override
            public Integer run() {
                runnable.run();
                return 1;
            }
        }, null);

        async_w(() -> {
            if (!task.isExecuted()) {
                TaskWorker taskWorker = YYStal.taskPool().getWorker(task);
                if (taskWorker != null) {
                    YYStal.taskPool().stopWorker(taskWorker, true);
                    YYStal.taskPool().newWorker();
                }
            }
        }, timeout);
    }


    // runs the function on the new thread and returns the future
    // this method does not use thread pool
    public static <X> Future<X> async_t(IFunction<X> function) {
        Future<X> future = new Future<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                future.setResult(function.run());
            }
        }).start();
        return future;
    }

    // runs the function on the new thread
    // this method does not use thread pool
    public static void async_t(Runnable runnable) {
        new Thread(runnable).start();
    }

    /* runs the function on the taskpool but with a delay */
    public static <X> Future<X> async_w(IFunction<X> function, long after) {
        Future<?> future = new Future<>();
        YYStal.taskPool().run_w(function, future, after);
        return (Future<X>) future;
    }

    /* runs the function on the taskpool but with a delay */
    public static void async_w(Runnable function, long after) {
        YYStal.taskPool().run_w(new IFunction<Integer>() {
            @Override
            public Integer run() {
                function.run();
                return 1;
            }
        }, null, after);
    }

    /*
    runs the function on the taskpool async and blocks the running thread
    till the function has executed and returns the value
     */
    public static <X> X await(IFunction<X> function) {
        Future<X> future = new Future<>();
        Task task = YYStal.taskPool().run(function, future);
        while (!task.isExecuted()) {
            kThread.sleep(YYStal.setting(YSettings.TASK_AWAIT_DELAY));
        }
        return future.result();
    }




    // runs the function on new thread and locks the current thread then returns the value
    public static <X> X await_t(IFunction<X> function) {
        prop<X> prop = new prop<>();
        prop<Boolean> executed = new prop<>(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                prop.set(function.run());
                executed.set(true);
            }
        }).start();
        while (!executed.get()) {
            kThread.sleep(YYStal.setting(YSettings.TASK_AWAIT_DELAY));
        }
        return prop.get();
    }
    // runs the function on new thread and locks the current thread till it finished
    public static void await_t(Runnable runnable) {
        prop<Boolean> executed = new prop<>(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runnable.run();
                executed.set(true);
            }
        }).start();
        while (!executed.get()) {
            kThread.sleep(YYStal.setting(YSettings.TASK_AWAIT_DELAY));
        }
    }

    /*
        runs the function on the taskpool async and blocks the running thread
        till the function has executed
     */
    public static void await(Runnable runnable) {
        Task task = YYStal.taskPool().run(new IFunction<Integer>() {
            @Override
            public Integer run() {
                runnable.run();
                return 1;
            }
        }, null);
        while (!task.isExecuted()) {
            kThread.sleep(YYStal.setting(YSettings.TASK_AWAIT_DELAY));
        }
    }

    /* runs the function as a loop async */
    public static Promise async_loop(Runnable runnable, long interval) {
        final Promise promise = new Promise();
        final prop<IFunction<Integer>> functionProp = new prop<>();

        functionProp.set(new IFunction<Integer>() {
            @Override
            public Integer run() {
                if (promise.isValid()) {
                    runnable.run();
                    async_w(functionProp.get(), interval);
                }
                return 1;
            }
        });

        async(functionProp.get());
        return promise;
    }

    /* runs the function as loop async but starts the loop with a delay same as interval value */
    public static Promise async_loop_w(Runnable runnable, long interval) {
        return async_loop_w(runnable, interval, interval);
    }

    /* runs the function as loop async but starts the loop with a delay */
    public static Promise async_loop_w(Runnable runnable, long interval, long startAfter) {
        final Promise promise = new Promise();
        final prop<IFunction<Integer>> functionProp = new prop<>();

        functionProp.set(new IFunction<Integer>() {
            @Override
            public Integer run() {
                if (promise.isValid()) {
                    runnable.run();
                    async_w(functionProp.get(), interval);
                }
                return 1;
            }
        });

        async_w(functionProp.get(), startAfter);
        return promise;
    }

    /* runs the loop async x times */
    public static Promise async_loop_times(Runnable runnable, long interval, int times) {
        final Promise promise = new Promise();
        final prop<IFunction<Integer>> functionProp = new prop<>();
        final prop<Integer> counter = new prop<>(times);
        functionProp.set(new IFunction<Integer>() {
            @Override
            public Integer run() {
                if (promise.isValid()) {
                    runnable.run();

                    counter.set(counter.get() + 1);
                    if (counter.get() >= times) {
                        promise.stop();
                        return 1;
                    }
                    async_w(functionProp.get(), interval);
                }
                return 1;
            }
        });

        async(functionProp.get());
        return promise;
    }

    /* runs the loop async x times with a startup delay (the interval) */
    public static Promise async_loop_times_w(Runnable runnable, long interval, int times) {
        return async_loop_times_w(runnable, interval, times, interval);
    }

    /* runs the loop async x times with a startup delay (the after) */
    public static Promise async_loop_times_w(Runnable runnable, long interval, int times, final long startAfter) {
        final Promise promise = new Promise();
        final prop<IFunction<Integer>> functionProp = new prop<>();
        final prop<Integer> counter = new prop<>(times);
        functionProp.set(new IFunction<Integer>() {
            @Override
            public Integer run() {
                if (promise.isValid()) {
                    runnable.run();

                    counter.set(counter.get() + 1);
                    if (counter.get() >= times) {
                        promise.stop();
                        return 1;
                    }
                    async_w(functionProp.get(), interval);
                }
                return 1;
            }
        });

        async_w(functionProp.get(), startAfter);
        return promise;
    }

    public static Promise async_loop_condition(Runnable runnable, long interval, IState condition) {
        final Promise promise = new Promise();
        final prop<IFunction<Integer>> functionProp = new prop<>();
        functionProp.set(new IFunction<Integer>() {
            @Override
            public Integer run() {
                if (promise.isValid() && condition.check()) {
                    runnable.run();

                    async_w(functionProp.get(), interval);
                }
                return 1;
            }
        });

        async(functionProp.get());
        return promise;
    }

    public static Promise async_loop_condition_w(Runnable runnable, long interval, IState condition) {
        return async_loop_condition_w(runnable, interval, condition, interval);
    }

    public static Promise async_loop_condition_w(Runnable runnable, long interval, IState condition, final long startAfter) {
        final Promise promise = new Promise();
        final prop<IFunction<Integer>> functionProp = new prop<>();
        functionProp.set(new IFunction<Integer>() {
            @Override
            public Integer run() {
                if (promise.isValid() && condition.check()) {
                    runnable.run();

                    async_w(functionProp.get(), interval);
                }
                return 1;
            }
        });

        async_w(functionProp.get(), startAfter);
        return promise;
    }


}
