package ic3.analyticsops.test;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.test.task.reporting.AOChromeException;
import ic3.analyticsops.test.task.reporting.AOChromeProxy;
import io.webfolder.cdp.session.Session;

import java.io.File;

public class AOTestContext
{
    private final AOTest test;

    private final AOChromeProxy chrome;

    public AOTestContext(AOTest test)
    {
        this.test = test;
        this.chrome = new AOChromeProxy();
    }

    public void shutdown()
    {
        chrome.shutdown();
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

    public void run()
            throws AORestApiException,
                   AOChromeException
    {
        test.run(this);
    }
}
