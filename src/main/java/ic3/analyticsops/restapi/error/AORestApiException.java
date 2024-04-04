package ic3.analyticsops.restapi.error;

import ic3.analyticsops.common.AOException;

public class AORestApiException extends AOException
{
    public AORestApiException(String message)
    {
        super(message);
    }

    public AORestApiException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
