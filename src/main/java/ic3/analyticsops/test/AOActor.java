package ic3.analyticsops.test;

import ic3.analyticsops.common.AOLoggers;
import org.jetbrains.annotations.Nullable;

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

        final String restApiURL = getRestApiURL(jsonParentTest.getRestApiURL());

        // Guess better to identify this issue as fast as possible.
        if (restApiURL == null)
        {
            throw new AOTestValidationException("the JSON field 'restApiURL' is missing from 'actors[" + jsonActorNb + "]' and test");
        }

        final AOAuthenticator authenticator = getAuthenticator(jsonParentTest.getAuthenticator());

        // Guess better to identify this issue as fast as possible : mandatory for the time being.
        if (authenticator == null)
        {
            throw new AOTestValidationException("the JSON field 'authenticator' is missing from 'actors[" + jsonActorNb + "]' and test");
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

    public boolean isActive()
    {
        return active == null || active;
    }

    @Nullable
    public String getRestApiURL(@Nullable String testRestApiURL)
    {
        return restApiURL != null ? restApiURL : testRestApiURL;
    }

    @Nullable
    public AOAuthenticator getAuthenticator(@Nullable AOAuthenticator testAuthenticator)
    {
        return authenticator != null ? authenticator : testAuthenticator;
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
    public void run(AOActorContext context)
    {
        final Long durationS = context.getDurationS();
        final Long expiryMS = durationS != null ? System.currentTimeMillis() + durationS * 1_000 : null;

        if (durationS != null)
        {
            AOLoggers.ACTOR.info("[actor] '{}' run for {} seconds", name, durationS);
        }
        else
        {
            AOLoggers.ACTOR.info("[actor] '{}' run once", name);
        }

        try
        {
            new Thread(() -> run(context, expiryMS), "actor-" + name).start();
        }
        catch (Throwable ex)
        {
            AOLoggers.ACTOR.error("[actor] '{}' run on-error", name);

            context.onActorError(ex);
            context.onActorCompleted();
        }
    }

    protected void run(AOActorContext context, @Nullable Long expiryMS)
    {
        AOLoggers.ACTOR.info("[actor] '{}' run started", name);

        do
        {
            try
            {
                context.clearTaskProperties();

                for (AOTask<?> task : tasks /* validated by now */)
                {
                    if (context.isOnError())
                    {
                        break /* i.e., another actor on error */;
                    }

                    final AOTaskContext tContext = new AOTaskContext(context, task);

                    try
                    {
                        task.run(tContext);

                        AOLoggers.ACTOR.debug("[actor] '{}.{}' ✓", name, task.getName());
                    }
                    catch (Throwable /* AssertionError */ ex)
                    {
                        AOLoggers.ACTOR.error("[actor] '{}' run on-task-error '{}'", name, task.getName(), ex);

                        context.onActorTaskError(task, ex);

                        break;
                    }
                }
            }
            catch (Throwable ex)
            {
                AOLoggers.ACTOR.error("[actor] '{}' run on-tasks-error", name, ex);

                context.onActorTasksError(ex);
            }
        }
        while (expiryMS != null /* single run */ && System.currentTimeMillis() < expiryMS && !context.isOnError());

        AOLoggers.ACTOR.info("[actor] '{}' run completed", name);

        context.onActorCompleted();
    }

}
