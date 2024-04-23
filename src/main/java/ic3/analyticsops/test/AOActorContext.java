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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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

    /**
     * For all the tasks : updated after each task's loop has been completed.
     */
    private final AtomicInteger runCount = new AtomicInteger();

    /**
     * For all the tasks : updated after each task's loop has been completed.
     */
    private final AtomicLong elapsedMS = new AtomicLong(-1);

    /**
     * For all the tasks : updated after each task's loop has been completed (value for 'runCount').
     */
    private final AtomicLong elapsedMSavg = new AtomicLong(-1);

    /**
     * For all the tasks : updated after each task's loop has been completed (value for 'runCount').
     */
    private final AtomicLong elapsedMSmax = new AtomicLong(-1);

    /**
     * For all the tasks : updated after each task's loop has been completed (value for 'runCount').
     */
    private final AtomicLong elapsedMSmin = new AtomicLong(-1);

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

    public AOActor getActor()
    {
        return actor;
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

    public int getRunCount()
    {
        return runCount.get();
    }

    /**
     * @return -1 means not relevant and should be ignored
     */
    public long getElapsedMS()
    {
        return elapsedMS.get();
    }

    /**
     * @return -1 means not relevant and should be ignored
     */
    public long getElapsedMSavg()
    {
        return elapsedMSavg.get();
    }

    /**
     * @return -1 means not relevant and should be ignored
     */
    public long getElapsedMSmax()
    {
        return elapsedMSmax.get();
    }

    /**
     * @return -1 means not relevant and should be ignored
     */
    public long getElapsedMSmin()
    {
        return elapsedMSmin.get();
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

    public void onActorStarted()
    {
        context.onActorStarted(actor);
    }

    /**
     * Still in the actor thread.
     */
    public void onActorCompleted()
    {
        context.onActorCompleted(actor);
    }

    public void onBeforeRunTasks()
    {
        taskProperties.clear();
    }

    public void onAfterRunTasks()
    {
        final long[] elapsedMS = new long[1];
        final long[] elapsedMSavg = new long[1];
        final long[] elapsedMSmax = new long[1];
        final long[] elapsedMSmin = new long[1];

        taskGauges.forEach((task, gauge) ->
        {
            elapsedMS[0] += gauge.getRunElapsedMS();
            elapsedMSavg[0] += gauge.getRunElapsedMSavg();
            elapsedMSmax[0] += gauge.getRunElapsedMSmax();
            elapsedMSmin[0] += gauge.getRunElapsedMSmin();
        });

        runCount.incrementAndGet();

        this.elapsedMS.set(elapsedMS[0]);
        this.elapsedMSavg.set(elapsedMSavg[0]);
        this.elapsedMSmax.set(elapsedMSmax[0]);
        this.elapsedMSmin.set(elapsedMSmin[0]);

        // TODO [load-testing] have some logs here to troubleshoot the stats at then end
        //      => should be consistent w/ the Ticks !
        //      => then multi-tasks
        //      => then multi-actors
        //      :make the tick period as a configuration (1 sec. might be very low for long running tests)
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
        AOLog4jUtils.TEST.debug(
                "[test] {} : run-count:{} avg.:{} max.:{} min.:{}",
                String.format("%20.20s", actor.getName()),
                runCount,
                AODurationUtils.formatMillis(elapsedMSavg.get()),
                AODurationUtils.formatMillis(elapsedMSmax.get()),
                AODurationUtils.formatMillis(elapsedMSmin.get())
        );

        final List<AOTask<?>> sortedTasks = taskGauges.keySet().stream()
                .sorted(Comparator.comparing(AOTask::getName))
                .toList();

        for (AOTask<?> task : sortedTasks)
        {
            final AOTaskGauge gauge = taskGauges.get(task);

            AOLog4jUtils.TEST.debug(
                    "[test] {} : run-count:{} avg.:{} max.:{} min.:{}",
                    String.format("%20.20s", task.getName()),
                    gauge.getRunCount(),
                    AODurationUtils.formatMillis(gauge.getRunElapsedMSavg()),
                    AODurationUtils.formatMillis(gauge.getRunElapsedMSmax()),
                    AODurationUtils.formatMillis(gauge.getRunElapsedMSmin())
            );
        }
    }

}
