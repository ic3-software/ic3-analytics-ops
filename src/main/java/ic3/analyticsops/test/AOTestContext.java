package ic3.analyticsops.test;

import ic3.analyticsops.stats.AOBigBrother;
import ic3.analyticsops.test.load.AOLoadTestConfiguration;
import ic3.analyticsops.test.schedule.AOTestSchedule;
import ic3.analyticsops.test.task.reporting.AOChromeException;
import ic3.analyticsops.test.task.reporting.AOChromeProxy;
import ic3.analyticsops.utils.AOLog4jUtils;
import io.webfolder.cdp.session.Session;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AOTestContext
{
    private final AOBigBrother bigBrother;

    private final AOTest test;

    private final AOTestSchedule schedule;

    private final AOChromeProxy chrome;

    private final CountDownLatch completion;

    private final AtomicBoolean onError = new AtomicBoolean(false);

    private final ReentrantLock statusLOCK = new ReentrantLock();

    private final Condition statusCondition = statusLOCK.newCondition();

    public AOTestContext(AOTest test, AOTestSchedule schedule)
    {
        this.bigBrother = new AOBigBrother(this);

        this.test = test;
        this.schedule = schedule;

        this.chrome = new AOChromeProxy(test.getChromeConfiguration());

        this.completion = new CountDownLatch(schedule.getActorCount());
    }

    public AOTestSchedule getSchedule()
    {
        return schedule;
    }

    public File getMDXesDataFolder(String data)
    {
        return test.getMDXesDataFolder(data);
    }

    @Nullable
    public Double getLoadFailAtCpuLoad()
    {
        final AOLoadTestConfiguration load = test.getLoad();

        if (load != null)
        {
            return load.getFailAtCpuLoad();
        }

        return null;
    }

    public String createBrowserContext()
            throws AOChromeException
    {
        return chrome.createBrowserContext();
    }

    public void disposeBrowserContext(String browserContext)
    {
        chrome.disposeBrowserContext(browserContext);
    }

    public Session createBrowserSession(String browserContext)
            throws AOChromeException
    {
        return chrome.createBrowserSession(browserContext);
    }

    public boolean isOnError()
    {
        return onError.get();
    }

    public void setOnError()
    {
        onError.set(true);

        statusLOCK.lock();

        try
        {
            statusCondition.signalAll();
        }
        finally
        {
            statusLOCK.unlock();
        }
    }

    public void onTestInterrupted(InterruptedException ex)
    {
        setOnError();
    }

    public void onActorError(AOActor actor, Throwable ex)
    {
        setOnError();
    }

    public void onActorTaskError(AOActor actor, AOTask task, Throwable ex)
    {
        setOnError();
    }

    public void onActorTasksError(AOActor actor, Throwable ex)
    {
        setOnError();
    }

    public void onLoadCpuLoadError()
    {
        setOnError();
    }

    public void onActorCompleted(AOActor actor)
    {
        completion.countDown();
    }

    public void waitForCompletion()
            throws InterruptedException
    {
        completion.await();
    }

    public void pause(long pauseMS)
    {
        if (pauseMS <= 0)
        {
            return;
        }

        final long deadlineMS = System.currentTimeMillis() + pauseMS;

        while (!isOnError() && System.currentTimeMillis() < deadlineMS)
        {
            final long actualPauseMS = deadlineMS - System.currentTimeMillis();

            if (actualPauseMS > 0)
            {
                try
                {
                    statusLOCK.lock();

                    try
                    {
                        statusCondition.await(actualPauseMS, TimeUnit.MILLISECONDS);
                    }
                    finally
                    {
                        statusLOCK.unlock();
                    }
                }
                catch (InterruptedException ignored)
                {
                }
            }
        }
    }

    /**
     * Blocking call.
     */
    public void run()
            throws InterruptedException
    {
        bigBrother.start();

        test.run(this);
    }

    public void shutdown()
    {
        try
        {
            chrome.shutdown();
        }
        catch (Exception ex)
        {
            AOLog4jUtils.SHELL.warn("[shell] chrome shutdown on error", ex);
        }

        try
        {
            bigBrother.shutdown();
        }
        catch (Exception ex)
        {
            AOLog4jUtils.SHELL.warn("[shell] big-brother shutdown on error", ex);
        }
    }
}
