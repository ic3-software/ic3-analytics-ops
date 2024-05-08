package ic3.analyticsops.test;

import ic3.analyticsops.utils.AOLog4jUtils;
import ic3.analyticsops.utils.AOThreadUtils;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public class AOActor extends AOSerializable
{
    /**
     * Final semantic: dunno how to set with JSON deserialization.
     */
    protected transient AOTest jsonParentTest;

    /**
     * Final semantic: dunno how to set with JSON deserialization.
     */
    protected transient int jsonActorNb;

    private final String name;

    @Nullable
    private final Boolean active;

    /**
     * Overriding the one defined at test level : handy for testing several remote (scaling-up) containers.
     */
    @Nullable
    private final String restApiURL;

    /**
     * Overriding the one defined at test level : handy for testing several security profiles.
     */
    @Nullable
    private final AOAuthenticator authenticator;

    /**
     * REST API request timeout.
     * Overriding the one defined at test level.
     */
    @Nullable
    private final Duration timeout;

    /**
     * Writes to the log the actual JSON returned by the server.
     */
    @Nullable
    private final Boolean dumpJson;

    /**
     * Writes to the log a pretty-print version of the payload of the JSON reply.
     */
    @Nullable
    private final Boolean dumpResult;

    private final List<AOTask<?>> tasks;

    protected AOActor()
    {
        // JSON deserialization

        this.name = null;
        this.active = null;
        this.restApiURL = null;
        this.authenticator = null;
        this.timeout = null;
        this.dumpJson = null;
        this.dumpResult = null;
        this.tasks = null;
    }

    /**
     * Called once deserialized to create some backlinks and array information.
     */
    public void onFromJson(AOTest jsonParent, int jsonActorNb)
    {
        this.jsonParentTest = jsonParent;
        this.jsonActorNb = jsonActorNb;

        if (tasks != null)
        {
            if (!tasks.isEmpty() && tasks.getLast() == null)
            {
                tasks.removeLast() /* trailing comma in JSON5 [] */;
            }

            for (int tt = 0; tt < tasks.size(); tt++)
            {
                final AOTask<?> task = tasks.get(tt);
                task.onFromJson(this, tt);
            }
        }
    }

    /**
     * Called once deserialized (after onFromJson) to ensure the JSON5 is valid.
     */
    public void validate()
            throws AOTestValidationException
    {
        validateNonEmptyField(validateFieldPathPrefix() + "name", name);

        final String restApiURL = getRestApiURL();

        // Guess better to identify this issue as fast as possible.
        if (restApiURL == null)
        {
            throw new AOTestValidationException("the JSON field 'restApiURL' is missing from 'actors[" + jsonActorNb + "]' and test");
        }

        final AOAuthenticator authenticator = getAuthenticator();

        // Guess better to identify this issue as fast as possible : mandatory for the time being.
        if (authenticator == null)
        {
            throw new AOTestValidationException("the JSON field 'authenticator' is missing from 'actors[" + jsonActorNb + "]' and test");
        }
        else
        {
            authenticator.validate(validateFieldPathPrefix() + "authenticator.");
        }

        validateTasks();
    }

    public void validateTasks()
            throws AOTestValidationException
    {
        validateNonEmptyField(validateFieldPathPrefix() + "tasks", tasks);

        for (AOTask<?> task : tasks)
        {
            task.validate();
        }
    }

    public String validateFieldPathPrefix()
    {
        return "actors[" + jsonActorNb + "].";
    }

    public String getName()
    {
        return name;
    }

    public boolean isActive()
    {
        return active == null || active;
    }

    @Nullable
    public String getRestApiURL()
    {
        return restApiURL != null ? restApiURL : jsonParentTest.getRestApiURL();
    }

    @Nullable
    public AOAuthenticator getAuthenticator()
    {
        return authenticator != null ? authenticator : jsonParentTest.getAuthenticator();
    }

    @Nullable
    public AOAuthenticator getElevatedAuthenticator()
    {
        return jsonParentTest != null ? jsonParentTest.getElevatedAuthenticator() : null;
    }

    @Nullable
    public Duration getTimeout()
    {
        return timeout != null ? timeout : jsonParentTest.getTimeout();
    }

    public boolean isDumpJson()
    {
        return dumpJson != null && dumpJson;
    }

    public boolean isDumpResult()
    {
        return dumpResult != null && dumpResult;
    }

    /**
     * Runs in its own thread of control.
     */
    public void run(AOActorContext context, long testStartMS)
    {
        try
        {
            AOThreadUtils.startNewThread(context.createThreadName(), () ->
            {
                try
                {
                    final long ms = System.currentTimeMillis() % 1000;
                    Thread.sleep(1000 - ms);
                }
                catch (InterruptedException ignored)
                {
                }

                doRun(context, testStartMS);
            });
        }
        catch (Throwable ex)
        {
            AOLog4jUtils.ACTOR.error("[actor] '{}' run on-error", name);

            context.onActorError(ex);
            context.onActorCompleted();
        }
    }

    protected void doRun(AOActorContext context, long testStartMS)
    {
        final boolean isOnce = context.isOnce();

        final long initialPauseMS = !isOnce ? context.getStartMS(testStartMS) - System.currentTimeMillis() : 0;

        context.pause(initialPauseMS);

        final long endMS = !isOnce ? context.getEndMS(testStartMS) : 0;

        AOLog4jUtils.ACTOR.info("[actor] '{}' run started ( init. pause : {} )", name, initialPauseMS);

        context.onActorStarted();

        do
        {
            try
            {
                context.onBeforeRunTasks();

                for (AOTask<?> task : tasks /* validated by now */)
                {
                    if (context.isOnError())
                    {
                        break /* i.e., another actor on error */;
                    }

                    final AOTaskContext tContext = new AOTaskContext(context, task);

                    try
                    {
                        context.onBeforeRunTask(task);

                        try
                        {
                            task.run(tContext);

                            AOLog4jUtils.ACTOR.debug("[actor] '{}.{}' ✓", name, task.getName());

                            final Long pauseMS = task.getPauseMS();

                            if (pauseMS != null)
                            {
                                final long startPauseMS = System.currentTimeMillis();

                                context.pause(pauseMS);

                                context.onRunTaskPaused(task, System.currentTimeMillis() - startPauseMS);
                            }
                        }
                        finally
                        {
                            context.onAfterRunTask(task);
                        }

                        context.assertPerformanceTargets(task) /* after .onAfterRunTask() */;

                    }
                    catch (Throwable /* AssertionError */ ex)
                    {
                        AOLog4jUtils.ACTOR.error("[actor] '{}' run on-task-error '{}'", name, task.getName(), ex);

                        context.onActorTaskError(task, ex);

                        break;
                    }
                }

                context.onAfterRunTasks();
            }
            catch (Throwable ex)
            {
                AOLog4jUtils.ACTOR.error("[actor] '{}' run on-tasks-error", name, ex);

                context.onActorTasksError(ex);
            }
        }
        while (!isOnce && System.currentTimeMillis() < endMS && !context.isOnError());

        if (!context.isOnError())
        {
            for (AOTask<?> task : tasks /* validated by now */)
            {
                try
                {
                    context.assertPerformanceTargetsEnd(task);
                }
                catch (Throwable /* AssertionError */ ex)
                {
                    AOLog4jUtils.ACTOR.error("[actor] '{}' run on-task-error '{}'", name, task.getName(), ex);

                    context.onActorTaskError(task, ex);
                }
            }
        }

        AOLog4jUtils.ACTOR.info("[actor] '{}' run completed", name);

        context.onActorCompleted();
    }

}
