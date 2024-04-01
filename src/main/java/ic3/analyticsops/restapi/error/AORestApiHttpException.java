package ic3.analyticsops.restapi.error;

public class AORestApiHttpException extends AORestApiException
{
    private final int statusCode;

    public AORestApiHttpException(int statusCode)
    {
        super("HTTP error : " + statusCode);

        this.statusCode = statusCode;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

}
