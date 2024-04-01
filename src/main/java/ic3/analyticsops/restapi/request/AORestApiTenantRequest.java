package ic3.analyticsops.restapi.request;

public abstract class AORestApiTenantRequest<REPLY> extends AORestApiRequest<REPLY>
{
    public AORestApiTenantRequest(String command, Class<REPLY> reply)
    {
        super(command, reply);
    }

}
