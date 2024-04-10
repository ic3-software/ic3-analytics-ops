package ic3.analyticsops.test.task.server;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.reply.table.AORestApiServerStatusTable;
import ic3.analyticsops.restapi.request.AORestApiServerStatusRequest;
import ic3.analyticsops.test.AOAssertion;
import ic3.analyticsops.test.AOAssertionMode;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.assertion.AOServerStatusAssertion;

import java.util.List;

public class AOServerStatusTask extends AOTask<AOServerStatusAssertion>
{
    protected AOServerStatusTask()
    {
        // JSON deserialization
    }

    @Override
    public String getKind()
    {
        return "ServerStatus";
    }

    @Override
    public AOAssertionMode getAssertionsMode()
    {
        return AOAssertionMode.OPTIONAL;
    }

    @Override
    public void run(AOTaskContext context)
            throws AOException
    {
        final AORestApiServerStatusTable reply = context.sendRequest(

                new AORestApiServerStatusRequest()

        );

        AOAssertion.assertNotNull("pid", reply.getPID());

        final List<AOServerStatusAssertion> assertions = getOptionalAssertions();

        if (assertions != null && !assertions.isEmpty())
        {
            for (AOServerStatusAssertion assertion : assertions)
            {
                assertion.assertOk(reply);
            }
        }
    }

}
