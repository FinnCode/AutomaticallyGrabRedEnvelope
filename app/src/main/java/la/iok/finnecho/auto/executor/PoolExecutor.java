package la.iok.finnecho.auto.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Finn on 2016/9/21 0021.
 */

public class PoolExecutor {
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    public static void schedule(Runnable runnable, long delay) {
        executor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }
}
