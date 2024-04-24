package ic3.analyticsops.test;

/**
 * Thread-safe to make it accessible from test level to aggregate ongoing statistics.
 */
public class AOTaskGauge
{
    private final AOTask<?> task;

    /**
     * Nothing fancy : ensure to aggregate at test (while running) level consistant values.
     */
    private final Object lock = new Object();

    private int runCount;

    private long runElapsedMS;

    private long runElapsedMStotal;

    private long runElapsedMSavg;

    private long runElapsedMSmax = Long.MIN_VALUE;

    private long runElapsedMSmin = Long.MAX_VALUE;

    private long runStartMS;

    private long runPausedMS;

    public AOTaskGauge(AOTask<?> task)
    {
        this.task = task;
    }

    public void onBeforeRunTask()
    {
        synchronized (lock)
        {
            runStartMS = System.currentTimeMillis();
            runPausedMS = 0;

            runElapsedMS = 0;
        }
    }

    public void onRunTaskPaused(long elapsedMS)
    {
        synchronized (lock)
        {
            runPausedMS += elapsedMS;
        }
    }

    public void onAfterRunTask()
    {
        synchronized (lock)
        {
            final long totalElapsedMS = System.currentTimeMillis() - runStartMS;
            final long elapsedMS = totalElapsedMS - runPausedMS;

            runStartMS = 0;
            runPausedMS = 0;

            runCount++;

            runElapsedMS = elapsedMS;

            runElapsedMStotal += elapsedMS;
            runElapsedMSavg = runElapsedMStotal / runCount;
            runElapsedMSmax = Math.max(runElapsedMSmax, elapsedMS);
            runElapsedMSmin = Math.min(runElapsedMSmin, elapsedMS);
        }
    }

    public int getRunCount()
    {
        synchronized (lock)
        {
            return runCount;
        }
    }

    public long getRunElapsedMS()
    {
        synchronized (lock)
        {
            return runElapsedMS;
        }
    }

    public long getRunElapsedMStotal()
    {
        synchronized (lock)
        {
            return runElapsedMStotal;
        }
    }

    public long getRunElapsedMSavg()
    {
        synchronized (lock)
        {
            return runElapsedMSavg;
        }
    }

    public long getRunElapsedMSmax()
    {
        synchronized (lock)
        {
            return runElapsedMSmax;
        }
    }

    public long getRunElapsedMSmin()
    {
        synchronized (lock)
        {
            return runElapsedMSmin;
        }
    }
}
