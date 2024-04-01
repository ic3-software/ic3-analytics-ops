package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLifeCycle;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLoadStatus;
import ic3.analyticsops.restapi.request.AORestApiUnloadSchemaRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.assertion.AOAssertion;

public class AOUnloadSchemaTask extends AOTask
{
    private String schemaName;

    @Override
    public String getKind()
    {
        return "UnloadSchema";
    }

    public void run(AOTaskContext context)
            throws AORestApiException
    {
        final AORestApiSchemaLifeCycle reply = context.sendRequest(

                new AORestApiUnloadSchemaRequest()
                        .schemaName(schemaName)

        );

        final AORestApiSchemaLoadStatus actualStatus = reply.status;

        // UNLOADED | NOP if not loaded

        if (actualStatus != AORestApiSchemaLoadStatus.NOP)
        {
            AOAssertion.assertEquals("unload-schema-status", AORestApiSchemaLoadStatus.UNLOADED, actualStatus);
        }

        AOAssertion.assertNull("unload-schema-error", reply.errors);
    }

}
