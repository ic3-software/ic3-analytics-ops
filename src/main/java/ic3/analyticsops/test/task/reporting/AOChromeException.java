package ic3.analyticsops.test.task.reporting;

import ic3.analyticsops.common.AOException;

public class AOChromeException extends AOException
{
    public AOChromeException(String message)
    {
        super(message);
    }

    public AOChromeException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
