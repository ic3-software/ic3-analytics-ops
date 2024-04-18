package ic3.analyticsops.test;

/**
 * Thread-safe to make it accessible from test level to aggregate ongoing statistics.
 */
public class AOTaskCounter
{
    private final AOTask<?> task;

    /**
     * Nothing fancy : ensure to aggregate at test (while running) level consistant values.
     */
    private final Object lock = new Object();

    private int runCount;

    /**
     * Total elapsed time for the 'runCount' runs.
     */
    private long runElapsedMStotal;

    private long runElapsedMSmin = Long.MAX_VALUE;

    private long runElapsedMSmax = Long.MIN_VALUE;

    private long runElapsedMSavg;

    private long runStartMS;

    private long runPausedMS;

    public AOTaskCounter(AOTask<?> task)
    {
        this.task = task;
    }

    public void onBeforeRun()
    {
        synchronized (lock)
        {
            runStartMS = System.currentTimeMillis();
            runPausedMS = 0;
        }
    }

    public void onRunPaused(long elapsedMS)
    {
        synchronized (lock)
        {
            runPausedMS += elapsedMS;
        }
    }

    public void onAfterRun()
    {
        synchronized (lock)
        {
            final long totalElapsedMS = System.currentTimeMillis() - runStartMS;
            final long elapsedMS = totalElapsedMS - runPausedMS;

            runStartMS = 0;
            runPausedMS = 0;

            runCount++;

            runElapsedMStotal += elapsedMS;
            runElapsedMSmin = Math.min(runElapsedMSmin, elapsedMS);
            runElapsedMSmax = Math.max(runElapsedMSmax, elapsedMS);
            runElapsedMSavg = runElapsedMStotal / runCount;
        }
    }

    public int getRunCount()
    {
        synchronized (lock)
        {
            return runCount;
        }
    }

    public long getRunElapsedMStotal()
    {
        synchronized (lock)
        {
            return runElapsedMStotal;
        }
    }

    public long getRunElapsedMSmin()
    {
        synchronized (lock)
        {
            return runElapsedMSmin;
        }
    }

    public long getRunElapsedMSmax()
    {
        synchronized (lock)
        {
            return runElapsedMSmax;
        }
    }

    public long getRunElapsedMSavg()
    {
        synchronized (lock)
        {
            return runElapsedMSavg;
        }
    }
}
