package ic3.analyticsops.restapi.reply;

import ic3.analyticsops.restapi.error.AORestApiError;

public abstract class AORestApiReply<PAYLOAD>
{
    public AORestApiReply()
    {
    }

    public abstract boolean isOK();

    public abstract boolean isError();

    public abstract AORestApiError getError();

    public abstract PAYLOAD getPayload();

}
