package ic3.analyticsops.restapi.reply;

import ic3.analyticsops.restapi.error.AORestApiError;

public class AORestApiErrorReply<PAYLOAD> extends AORestApiReply<PAYLOAD>
{
    private final AORestApiError error;

    public AORestApiErrorReply(AORestApiError error)
    {
        this.error = error;
    }

    public boolean isError()
    {
        return true;
    }

    public AORestApiError getError()
    {
        return error;
    }

    @Override
    public boolean isOK()
    {
        return false;
    }

    public PAYLOAD getPayload()
    {
        throw new RuntimeException("internal error: inconsistent usage of success reply getPayload");
    }

}
