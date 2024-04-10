package ic3.analyticsops.test.task.server;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.request.AORestApiClearResultCacheRequest;
import ic3.analyticsops.test.AOAssertionMode;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.AOTestValidationException;

/**
 * Both OpenReport and PrintReport are possibly using MDX results from the result cache.
 */
public class AOClearResultCacheTask extends AOTask
{
    protected AOClearResultCacheTask()
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
        return "ClearResultCache";
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

                new AORestApiClearResultCacheRequest()

        );

        // Unless an exception has been thrown the cache should have been cleared by now.

    }

}
