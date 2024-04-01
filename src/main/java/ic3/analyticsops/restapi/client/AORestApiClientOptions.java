package ic3.analyticsops.restapi.client;

public class AORestApiClientOptions
{
    public boolean dumpJson;

    public boolean withJson;

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
