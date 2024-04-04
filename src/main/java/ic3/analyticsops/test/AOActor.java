package ic3.analyticsops.test;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.client.AORestApiClientOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AOActor extends AOSerializable
{
    public static final Logger LOGGER = LogManager.getLogger();

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

    private final List<AOTask<?>> tasks;

    protected AOActor()
    {
        // JSON deserialization

        this.name = null;
        this.active = null;
        this.restApiURL = null;
        this.authenticator = null;
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

    public void run(AOActorContext context)
            throws AOException
    {
        context.clearTaskProperties();

        for (AOTask<?> task : tasks /* validated by now */)
        {
            final AORestApiClientOptions options = new AORestApiClientOptions()
                    .dumpJson(task.isDumpJson());

            final AOTaskContext tContext = new AOTaskContext(context, options);

            task.run(tContext);

            LOGGER.info(name + " : " + task.getName() + " ✓");
        }
    }

}
