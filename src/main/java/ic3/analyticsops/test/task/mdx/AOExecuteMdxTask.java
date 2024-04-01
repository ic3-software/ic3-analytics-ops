package ic3.analyticsops.test.task.mdx;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxScriptResult;
import ic3.analyticsops.restapi.request.AORestApiExecuteMdxRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.assertion.AOExecuteMdxAssertion;

import java.util.List;

public class AOExecuteMdxTask extends AOTask<AOExecuteMdxAssertion>
{
    private String schema;

    private String statement;

    @Override
    public String getKind()
    {
        return "ExecuteMDX";
    }

    @Override
    public void run(AOTaskContext context)
            throws AORestApiException
    {
        final AORestApiMdxScriptResult reply = context.sendRequest(

                new AORestApiExecuteMdxRequest()
                        .schema(schema)
                        .mdx(statement)

        );

        final List<AOExecuteMdxAssertion> assertions = assertions();

        if (assertions != null)
        {
            for (AOExecuteMdxAssertion assertion : assertions)
            {
                assertion.assertOk(context, schema, reply);
            }
        }
    }
}
