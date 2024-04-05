package ic3.analyticsops.test;

import ic3.analyticsops.common.AOLoggers;
import ic3.analyticsops.test.task.reporting.AOChromeException;
import ic3.analyticsops.test.task.reporting.AOChromeProxy;
import io.webfolder.cdp.session.Session;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class AOTestContext
{
    private final AOTest test;

    private final AOChromeProxy chrome;

    private final CountDownLatch completion;

    private final AtomicBoolean onError = new AtomicBoolean(false);

    public AOTestContext(AOTest test)
    {
        this.test = test;
        this.chrome = new AOChromeProxy();
        this.completion = new CountDownLatch(test.activeActors().size());
    }

    @Nullable
    public Long getDurationS()
    {
        return test.getDurationS();
    }

    public File getMDXesDataFolder(String data)
    {
        return test.getMDXesDataFolder(data);
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

    public void onTestInterrupted(InterruptedException ex)
    {
        onError.set(true);
    }

    public void onActorError(AOActor actor, Throwable ex)
    {
        onError.set(true);
    }

    public void onActorTaskError(AOActor actor, AOTask task, Throwable ex)
    {
        onError.set(true);
    }

    public void onActorTasksError(AOActor actor, Throwable ex)
    {
        onError.set(true);
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

    public void shutdown()
    {
        try
        {
            chrome.shutdown();
        }
        catch (Exception ex)
        {
            AOLoggers.SHELL.warn("[shell] shutdown on error", ex);
        }
    }
}
