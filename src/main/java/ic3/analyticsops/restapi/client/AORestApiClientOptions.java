package ic3.analyticsops.restapi.client;

import ic3.analyticsops.test.AOAuthenticator;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class AORestApiClientOptions
{
    @Nullable
    public AOAuthenticator authenticator;

    @Nullable
    public Duration timeout;

    public boolean dumpJson;

    public boolean withJson;

    public AORestApiClientOptions authenticator(@Nullable AOAuthenticator authenticator)
    {
        this.authenticator = authenticator;
        return this;
    }

    public AORestApiClientOptions timeout(@Nullable Duration timeout)
    {
        this.timeout = timeout;
        return this;
    }

    public AORestApiClientOptions dumpJson(boolean flag)
    {
        this.dumpJson = flag;
        return this;
    }

    public AORestApiClientOptions withJson(boolean flag)
    {
        this.withJson = flag;
        return this;
    }
}
