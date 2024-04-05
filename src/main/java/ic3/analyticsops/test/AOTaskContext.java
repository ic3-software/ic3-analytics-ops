package ic3.analyticsops.test;

import ic3.analyticsops.restapi.client.AORestApiClientOptions;
import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.request.AORestApiRequest;
import ic3.analyticsops.test.task.reporting.AOChromeException;
import io.webfolder.cdp.session.Session;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class AOTaskContext
{
    private final AOActorContext context;

    private final AORestApiClientOptions options;

    public AOTaskContext(AOActorContext context, @Nullable AORestApiClientOptions options)
    {
        this.context = context;
        this.options = options;
    }

    public String getRestApiURL()
    {
        return context.getRestApiURL();
    }

    public File getMDXesDataFolder(String data)
    {
        return context.getMDXesDataFolder(data);
    }

    public String createBrowserContext()
            throws AOChromeException
    {
        return context.createBrowserContext();
    }

    public void disposeBrowserContext(String browserContext)
    {
        context.disposeBrowserContext(browserContext);
    }

    public Session createBrowserSession(String browserContext)
            throws AOChromeException
    {
        return context.createBrowserSession(browserContext);
    }

    public void setTaskProperty(String name, String value)
    {
        context.setTaskProperty(name, value);
    }

    public String getTaskProperty(String name, String defaultValue)
    {
        return context.getTaskProperty(name, defaultValue);
    }

    /**
     * Blocking call.
     */
    public <REPLY> REPLY sendRequest(AORestApiRequest<REPLY> request)
            throws AORestApiException
    {
        return context.sendRequest(request, options);
    }

    /**
     * Blocking call.
     */
    public <REPLY> REPLY sendRequestWithJson(AORestApiRequest<REPLY> request)
            throws AORestApiException
    {
        return context.sendRequest(request, new AORestApiClientOptions()
                .dumpJson(options != null && options.dumpJson)
                .withJson(true)
        );
    }

    public boolean isOnError()
    {
        return context.isOnError();
    }
}
