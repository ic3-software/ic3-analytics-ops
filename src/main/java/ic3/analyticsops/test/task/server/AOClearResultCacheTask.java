package ic3.analyticsops.test.task.server;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.request.AORestApiClearResultCacheRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;

/**
 * Both OpenReport and PrintReport are possibly using MDX results from the result cache.
 */
public class AOClearResultCacheTask extends AOTask
{
    @Override
    public String getKind()
    {
        return "ClearResultCache";
    }

    @Override
    public void run(AOTaskContext context)
            throws AORestApiException
    {
        context.sendRequest(

                new AORestApiClearResultCacheRequest()

        );

        // Unless an exception has been thrown the cache should have been cleared by now.

    }

}
