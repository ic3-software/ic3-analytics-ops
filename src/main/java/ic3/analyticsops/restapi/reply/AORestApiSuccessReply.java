package ic3.analyticsops.restapi.reply;

import ic3.analyticsops.restapi.error.AORestApiError;

public class AORestApiSuccessReply<PAYLOAD> extends AORestApiReply<PAYLOAD>
{
    private final PAYLOAD payload;

    public AORestApiSuccessReply(PAYLOAD payload)
    {
        this.payload = payload;
    }

    @Override
    public boolean isOK()
    {
        return true;
    }

    public PAYLOAD getPayload()
    {
        return payload;
    }

    public boolean isError()
    {
        return false;
    }

    public AORestApiError getError()
    {
        throw new RuntimeException("internal error: inconsistent usage of success reply getError");
    }

}
