package ic3.analyticsops.test.task.mdx;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxScriptResult;
import ic3.analyticsops.restapi.request.AORestApiExecuteMdxRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.AOTestValidationException;
import ic3.analyticsops.test.assertion.AOExecuteMdxAssertion;

import java.util.List;

public class AOExecuteMdxTask extends AOTask<AOExecuteMdxAssertion>
{
    private final String schema;

    private final String statement;

    protected AOExecuteMdxTask()
    {
        // JSON deserialization

        this.schema = null;
        this.statement = null;
    }

    @Override
    public void validateProps()
            throws AOTestValidationException
    {
        super.validateProps();

        validateNonEmptyField(validateFieldPathPrefix() + "schema", schema);
        validateNonEmptyField(validateFieldPathPrefix() + "statement", statement);
    }

    @Override
    public String getKind()
    {
        return "ExecuteMDX";
    }

    @Override
    public boolean withAssertions()
    {
        return true;
    }

    @Override
    public void run(AOTaskContext context)
            throws AOException
    {
        context.markForActualResult();

        final AORestApiMdxScriptResult reply = context.sendRequest(

                new AORestApiExecuteMdxRequest()
                        .schema(schema)
                        .mdx(statement)

        );

        context.markForExpectedResult();

        final List<AOExecuteMdxAssertion> assertions = assertions();

        for (AOExecuteMdxAssertion assertion : assertions /* validated by now */)
        {
            assertion.assertOk(context, schema, reply);
        }
    }
}
