package ic3.analyticsops.test;

import ic3.analyticsops.restapi.client.AORestApiClient;
import ic3.analyticsops.restapi.client.AORestApiClientOptions;
import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.request.AORestApiRequest;
import ic3.analyticsops.test.task.reporting.AOChromeException;
import io.webfolder.cdp.session.Session;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class AOActorContext
{
    private final AOTestContext context;

    private final AORestApiClient client;

    public AOActorContext(AOTestContext context, AORestApiClient client)
    {
        this.context = context;
        this.client = client;
    }

    public String getRestApiURL()
    {
        return client.getRestApiURL();
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

    /**
     * Blocking call.
     */
    public <REPLY> REPLY sendRequest(AORestApiRequest<REPLY> request, @Nullable AORestApiClientOptions options)
            throws AORestApiException
    {
        return client.sendRequest(request, options);
    }

}
