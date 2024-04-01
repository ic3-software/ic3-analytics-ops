package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaStatus;
import ic3.analyticsops.restapi.reply.table.AORestApiSchemaInfoTable;
import ic3.analyticsops.restapi.request.AORestApiSchemaInfoRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.assertion.AOSchemaInfoAssertion;

import java.util.List;

public class AOSchemaInfoTask extends AOTask<AOSchemaInfoAssertion>
{
    private String schemaFile;

    @Override
    public String getKind()
    {
        return "SchemaInfo";
    }

    @Override
    public void run(AOTaskContext context)
            throws AORestApiException
    {
        final AORestApiSchemaInfoTable reply = context.sendRequest(

                new AORestApiSchemaInfoRequest()
                        .schemaFactoryFile(schemaFile)

        );

        // If the schema is unknown an exception has been thrown.

        final AORestApiSchemaStatus actualStatus = reply.getStatus();

        final List<AOSchemaInfoAssertion> assertions = assertions();

        if (assertions != null)
        {
            for (AOSchemaInfoAssertion assertion : assertions)
            {
                assertion.assertOk(actualStatus);
            }
        }
    }
}
