package ic3.analyticsops.restapi.error;

public class AORestApiErrorException extends AORestApiException
{
    private final AORestApiError error;

    public AORestApiErrorException(AORestApiError error)
    {
        super("API error : " + error.asExceptionMessage());

        this.error = error;
    }

    public AORestApiError getError()
    {
        return error;
    }

}
