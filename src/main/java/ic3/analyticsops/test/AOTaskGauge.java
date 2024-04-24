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

    private long elapsedMS;

    private long elapsedMStotal;

    private long elapsedMSavg;

    private long elapsedMSmax = Long.MIN_VALUE;

    private long elapsedMSmin = Long.MAX_VALUE;

    private long startMS;

    private long pausedMS;

    public AOTaskGauge(AOTask<?> task)
    {
        this.task = task;
    }

    public void onBeforeRunTask()
    {
        synchronized (lock)
        {
            startMS = System.currentTimeMillis();
            pausedMS = 0;

            elapsedMS = 0;
        }
    }

    public void onRunTaskPaused(long elapsedMS)
    {
        synchronized (lock)
        {
            pausedMS += elapsedMS;
        }
    }

    public void onAfterRunTask()
    {
        synchronized (lock)
        {
            final long nowMS = System.currentTimeMillis();
            final long ms = nowMS - startMS - pausedMS;

            startMS = 0;
            pausedMS = 0;

            runCount++;

            elapsedMS = ms;

            elapsedMStotal += elapsedMS;
            elapsedMSavg = elapsedMStotal / runCount;

            elapsedMSmax = Math.max(elapsedMSmax, elapsedMS);
            elapsedMSmin = Math.min(elapsedMSmin, elapsedMS);
        }
    }

    public int getRunCount()
    {
        synchronized (lock)
        {
            return runCount;
        }
    }

    public long getElapsedMS()
    {
        synchronized (lock)
        {
            return elapsedMS;
        }
    }

    public long getElapsedMStotal()
    {
        synchronized (lock)
        {
            return elapsedMStotal;
        }
    }

    public long getElapsedMSavg()
    {
        synchronized (lock)
        {
            return elapsedMSavg;
        }
    }

    public long getElapsedMSmax()
    {
        synchronized (lock)
        {
            return elapsedMSmax;
        }
    }

    public long getElapsedMSmin()
    {
        synchronized (lock)
        {
            return elapsedMSmin;
        }
    }
}
