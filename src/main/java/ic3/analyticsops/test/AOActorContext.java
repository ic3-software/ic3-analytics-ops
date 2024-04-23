package ic3.analyticsops.test;

import ic3.analyticsops.restapi.client.AORestApiClient;
import ic3.analyticsops.restapi.client.AORestApiClientOptions;
import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.request.AORestApiRequest;
import ic3.analyticsops.test.schedule.AOActorSchedule;
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

    private final AOActorSchedule schedule;

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
    private final Map<AOTask<?>, AOTaskGauge> taskGauges = new ConcurrentHashMap<>();

    public AOActorContext(AOTestContext context, AOActorSchedule schedule)
    {
        final AOActor actor = schedule.getActor();

        final String restApiURL = actor.getRestApiURL();
        final AOAuthenticator authenticator = actor.getAuthenticator();
        final Duration timeout = actor.getTimeout();

        final AORestApiClient client = new AORestApiClient(restApiURL, authenticator, timeout);

        this.context = context;
        this.client = client;
        this.actor = schedule.getActor();
        this.schedule = schedule;
    }

    public String createThreadName()
    {
        return actor.getName() + ".actor" + (schedule.getId() != -1 ? "." + schedule.getId() : "");
    }

    public boolean isOnce()
    {
        return schedule.isOnce();
    }

    public long getStartMS(long testStartMS)
    {
        return testStartMS + schedule.getStartMS();
    }

    public long getEndMS(long testStartMS)
    {
        return testStartMS + schedule.getEndMS();
    }

    public String getRestApiURL()
    {
        return client.getRestApiURL();
    }

    public AOAuthenticator getAuthenticator()
    {
        return client.getAuthenticator();
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
        final AOTaskGauge gauge = taskGauges.computeIfAbsent(task, t -> new AOTaskGauge(task));

        gauge.onBeforeRun();
    }

    public void onRunTaskPaused(AOTask<?> task, long elapsedMS)
    {
        final AOTaskGauge gauge = taskGauges.get(task);

        if (gauge == null)
        {
            throw new RuntimeException("unexpected missing gauge for task : " + task.getName());
        }

        gauge.onRunPaused(elapsedMS);
    }

    public void onAfterRunTask(AOTask<?> task)
    {
        final AOTaskGauge gauge = taskGauges.get(task);

        if (gauge == null)
        {
            throw new RuntimeException("unexpected missing gauge for task : " + task.getName());
        }

        gauge.onAfterRun();
    }

    public void pause(long pauseMS)
    {
        context.pause(pauseMS);
    }

    public void run(long testStartMS)
    {
        actor.run(this, testStartMS) /* in its own thread of control */;
    }

    public void assertPerformanceTargets(AOTask<?> task)
    {
        final AOPerformanceTargets targets = task.getPerformanceTargets();

        if (targets != null)
        {
            final AOTaskGauge gauge = taskGauges.get(task);

            if (gauge == null)
            {
                throw new RuntimeException("unexpected missing gauge for task : " + task.getName());
            }

            targets.assertOk(gauge, false);
        }
    }

    public void assertPerformanceTargetsEnd(AOTask<?> task)
    {
        final AOPerformanceTargets targets = task.getPerformanceTargets();

        if (targets != null)
        {
            final AOTaskGauge gauge = taskGauges.get(task);

            if (gauge == null)
            {
                throw new RuntimeException("unexpected missing gauge for task : " + task.getName());
            }

            targets.assertOk(gauge, true);
        }
    }

    public void dumpStatistics()
    {
        final List<AOTask<?>> sortedTasks = taskGauges.keySet().stream()
                .sorted(Comparator.comparing(AOTask::getName))
                .toList();

        for (AOTask<?> task : sortedTasks)
        {
            final AOTaskGauge gauge = taskGauges.get(task);

            AOLog4jUtils.ACTOR.debug(
                    "[actor] '{}' task '{}' statistics : run-count:{} avg.:{} max.:{} min.:{}",
                    actor.getName(),
                    task.getName(),
                    gauge.getRunCount(),
                    AODurationUtils.formatMillis(gauge.getRunElapsedMSavg()),
                    AODurationUtils.formatMillis(gauge.getRunElapsedMSmax()),
                    AODurationUtils.formatMillis(gauge.getRunElapsedMSmin())
            );
        }
    }

}
