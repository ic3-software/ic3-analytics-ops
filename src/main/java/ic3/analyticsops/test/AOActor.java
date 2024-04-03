package ic3.analyticsops.test;

import ic3.analyticsops.restapi.client.AORestApiClientOptions;
import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.test.task.reporting.AOChromeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AOActor
{
    public static final Logger LOGGER = LogManager.getLogger();

    private String name;

    @Nullable
    private Boolean active;

    /**
     * Overriding the one defined at test level : handy for testing several remote (scaling-up) containers.
     */
    @Nullable
    private String restApiURL;

    /**
     * Overriding the one defined at test level : handy for testing several security profiles.
     */
    @Nullable
    private AOAuthenticator authenticator;

    private List<AOTask<?>> tasks;

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
            throws AORestApiException,
                   AOChromeException
    {
        if (tasks != null)
        {
            context.clearTaskProperties();

            for (AOTask<?> task : tasks)
            {
                if (task != null /* trailing comma in JSON5 [] */)
                {
                    final AORestApiClientOptions options = new AORestApiClientOptions()
                            .dumpJson(task.isDumpJson());

                    final AOTaskContext tContext = new AOTaskContext(context, options);

                    task.run(tContext);

                    LOGGER.info(name + " : " + task.getName() + " ✓");
                }
            }
        }
    }

}
