package ic3.analyticsops.test;

import ic3.analyticsops.restapi.client.AORestApiClientOptions;
import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.request.AORestApiRequest;
import ic3.analyticsops.test.task.reporting.AOChromeException;
import ic3.analyticsops.utils.AOLog4jUtils;
import io.webfolder.cdp.session.Session;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class AOTaskContext
{
    private final AOActorContext context;

    private final AOTask<?> task;

    @Nullable
    private MarkedResult markedResult;

    public AOTaskContext(AOActorContext context, AOTask<?> task)
    {
        this.context = context;
        this.task = task;
    }

    public String getRestApiURL()
    {
        return context.getRestApiURL();
    }

    public AOAuthenticator getAuthenticator()
    {
        return context.getAuthenticator();
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

    public void markForActualResult()
    {
        markedResult = MarkedResult.actual;
    }

    public void markForExpectedResult()
    {
        markedResult = MarkedResult.expected;
    }

    /**
     * Blocking call.
     */
    public <REPLY> REPLY sendRequest(AORestApiRequest<REPLY> request)
            throws AORestApiException
    {
        final AORestApiClientOptions options = new AORestApiClientOptions()
                .timeout(task.getTimeout())
                .dumpJson(task.isDumpJson());

        return prettyPrint(context.sendRequest(request, options));
    }

    /**
     * Blocking call.
     */
    public <REPLY> REPLY sendRequestWithJson(AORestApiRequest<REPLY> request)
            throws AORestApiException
    {
        final AORestApiClientOptions options = new AORestApiClientOptions()
                .timeout(task.getTimeout())
                .dumpJson(task.isDumpJson())
                .withJson(true);

        return prettyPrint(context.sendRequest(request, options));
    }

    public <REPLY> REPLY prettyPrint(REPLY reply)
    {
        if (!task.isDumpResult())
        {
            return reply;
        }

        try
        {
            // NO base class for the reply payload :-(

            final Method prettyPrint = reply.getClass().getMethod("prettyPrint", PrintStream.class);

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final PrintStream ps = new PrintStream(out, true, StandardCharsets.UTF_8);

            prettyPrint.invoke(reply, ps);

            AOLog4jUtils.PRETTY_PRINT.warn("[pretty-print] {}{}\n{}", reply.getClass().getSimpleName(), markedResult != null ? "(" + markedResult + ")" : "", out.toString(StandardCharsets.UTF_8));
        }
        catch (Exception ex)
        {
            AOLog4jUtils.PRETTY_PRINT.warn("[pretty-print] pretty-print missing for class : {}{}", reply.getClass().getSimpleName(), markedResult != null ? "(" + markedResult + ")" : "");
        }

        return reply;
    }

    public boolean isOnError()
    {
        return context.isOnError();
    }

    public void pause(long pauseMS)
    {
        context.pause(pauseMS);
    }

    public void onRunPaused(long elapsedMS)
    {
        context.onRunTaskPaused(task, elapsedMS);
    }

    enum MarkedResult
    {
        expected, actual
    }
}
