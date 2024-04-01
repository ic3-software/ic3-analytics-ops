package ic3.analyticsops.restapi.error;

public class AORestApiError
{
    public final String code;

    public final String message;

    public final String detailedMessage;

    public AORestApiError(String code, String message, String detailedMessage)
    {
        this.code = code;
        this.message = message;
        this.detailedMessage = detailedMessage;
    }

    public String asExceptionMessage()
    {
        return code + " : " + message + " ( " + detailedMessage + " ) ";
    }

    @Override
    public String toString()
    {
        return code + " : " + message + " ( " + detailedMessage + " ) ";
    }
}
