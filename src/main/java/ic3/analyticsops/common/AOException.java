package ic3.analyticsops.common;

public class AOException extends Exception
{
    public AOException(String message)
    {
        super(message);
    }

    public AOException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
