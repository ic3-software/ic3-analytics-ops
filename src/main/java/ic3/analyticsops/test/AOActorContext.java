package ic3.analyticsops.test;

import ic3.analyticsops.restapi.client.AORestApiClient;
import ic3.analyticsops.restapi.client.AORestApiClientOptions;
import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.request.AORestApiRequest;
import ic3.analyticsops.test.task.reporting.AOChromeException;
import ic3.analyticsops.utils.AODurationUtils;
import ic3.analyticsops.utils.AOLog4jUtils;
import io.webfolder.cdp.session.Session;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AOActorContext
{
    private final AOTestContext context;

    private final AORestApiClient client;

    private final AOActor actor;

    /**
     * Properties that can be produced and consumed by task.
     * <pre>
     * For example, the task :
     * {
     *      action: "LoadSchema"
     *      schemaFile: "Sales__LiveDemo_.icc-schema",
     *      forceBackup: true
     * }
     *
     * is producing a property containing the timestamp of the generated backup name :
     *
     *      ${LoadSchema.Sales (LiveDemo).info} = ...
     *
     * that can be consumed by a following task as following :
     * {
     *      action: "DeleteSchemaBackup",
     *      schemaName: "Sales (LiveDemo)",
     *      timestamp: "${LoadSchema.Sales (LiveDemo).info}"
     * },
     * </pre>
     */
    private final Map<String, String> taskProperties = new HashMap<>();

    /**
     * Thread-safe to make it accessible from test level to aggregate ongoing statistics.
     */
    private final Map<AOTask<?>, AOTaskCounter> taskCounters = new ConcurrentHashMap<>();

    public AOActorContext(AOTestContext context, AORestApiClient client, AOActor actor)
    {
        this.context = context;
        this.client = client;
        this.actor = actor;
    }

    public String getRestApiURL()
    {
        return client.getRestApiURL();
    }

    public AOAuthenticator getAuthenticator()
    {
        return client.getAuthenticator();
    }

    @Nullable
    public Duration getDuration()
    {
        return context.getDuration();
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
        taskProperties.put(name, value);
    }

    public String getTaskProperty(String name, String defaultValue)
    {
        final String value = taskProperties.get(name);
        return value != null ? value : defaultValue;
    }

    /**
     * Blocking call.
     */
    public <REPLY> REPLY sendRequest(AORestApiRequest<REPLY> request, @Nullable AORestApiClientOptions options)
            throws AORestApiException
    {
        return client.sendRequest(request, options);
    }

    public boolean isOnError()
    {
        return context.isOnError();
    }

    public void onActorError(Throwable ex)
    {
        context.onActorError(actor, ex);
    }

    public void onActorTaskError(AOTask<?> task, Throwable ex)
    {
        context.onActorTaskError(actor, task, ex);
    }

    public void onActorTasksError(Throwable ex)
    {
        context.onActorTasksError(actor, ex);
    }

    /**
     * Still in the actor thread.
     */
    public void onActorCompleted()
    {
        context.onActorCompleted(actor);
    }

    public void onBeforeRunTasks(int run)
    {
        taskProperties.clear();
    }

    public void onBeforeRunTask(AOTask<?> task)
    {
        final AOTaskCounter counter = taskCounters.computeIfAbsent(task, t -> new AOTaskCounter(task));

        counter.onBeforeRun();
    }

    public void onRunTaskPaused(AOTask<?> task, long elapsedMS)
    {
        final AOTaskCounter counter = taskCounters.get(task);

        if (counter == null)
        {
            throw new RuntimeException("unexpected missing task counter : " + task.getName());
        }

        counter.onRunPaused(elapsedMS);
    }

    public void onAfterRunTask(AOTask<?> task)
    {
        final AOTaskCounter counter = taskCounters.get(task);

        if (counter == null)
        {
            throw new RuntimeException("unexpected missing task counter : " + task.getName());
        }

        counter.onAfterRun();
    }

    public void run()
    {
        actor.run(this) /* in its own thread of control */;
    }

    public void dumpStatistics()
    {
        final List<AOTask<?>> sortedTasks = taskCounters.keySet().stream()
                .sorted(Comparator.comparing(AOTask::getName))
                .toList();

        for (AOTask<?> task : sortedTasks)
        {
            final AOTaskCounter counter = taskCounters.get(task);

            AOLog4jUtils.ACTOR.debug(
                    "[actor] '{}' task '{}' statistics : run:{} avg:{} max:{} min:{}",
                    actor.getName(),
                    task.getName(),
                    counter.getRunCount(),
                    AODurationUtils.formatMillis(counter.getRunElapsedMSavg()),
                    AODurationUtils.formatMillis(counter.getRunElapsedMSmax()),
                    AODurationUtils.formatMillis(counter.getRunElapsedMSmin())
            );
        }
    }
}
