package ic3.analyticsops.test.task.server;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.table.AORestApiServerStatusTable;
import ic3.analyticsops.restapi.request.AORestApiServerStatusRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.assertion.AOAssertion;
import ic3.analyticsops.test.assertion.AOServerStatusAssertion;

import java.util.List;

public class AOServerStatusTask extends AOTask<AOServerStatusAssertion>
{
    @Override
    public String getKind()
    {
        return "ServerStatus";
    }

    @Override
    public void run(AOTaskContext context)
            throws AORestApiException
    {
        final AORestApiServerStatusTable reply = context.sendRequest(

                new AORestApiServerStatusRequest()

        );

        AOAssertion.assertNotNull("pid", reply.getPID());

        final List<AOServerStatusAssertion> assertions = assertions();

        if (assertions != null)
        {
            for (AOServerStatusAssertion assertion : assertions)
            {
                assertion.assertOk(reply);
            }
        }

    }

}
