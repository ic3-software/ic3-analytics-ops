package ic3.analyticsops.test.task.server;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.request.AORestApiClearOnTheFlyPermsCacheRequest;
import ic3.analyticsops.test.AOAssertionMode;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.AOTestValidationException;

public class AOClearOnTheFlyPermsCacheTask extends AOTask
{
    protected AOClearOnTheFlyPermsCacheTask()
    {
        // JSON deserialization
    }

    @Override
    public void validateProps()
            throws AOTestValidationException
    {
        super.validateProps();
    }

    @Override
    public String getKind()
    {
        return "ClearOnTheFlyPermsCache";
    }

    @Override
    public AOAssertionMode getAssertionsMode()
    {
        return AOAssertionMode.NONE;
    }

    @Override
    public void run(AOTaskContext context)
            throws AOException
    {
        context.sendRequest(

                new AORestApiClearOnTheFlyPermsCacheRequest()

        );

        // Unless an exception has been thrown the cache should have been cleared by now.

    }

}
