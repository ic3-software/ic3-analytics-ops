package ic3.analyticsops.restapi.error;

public class AORestApiException extends Exception
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
