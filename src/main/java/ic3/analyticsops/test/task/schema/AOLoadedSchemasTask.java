package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaStatus;
import ic3.analyticsops.restapi.reply.table.AORestApiPropertyTable;
import ic3.analyticsops.restapi.reply.table.AORestApiTableRowEx;
import ic3.analyticsops.restapi.request.AORestApiLoadedSchemasRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.assertion.AOAssertion;

public class AOLoadedSchemasTask extends AOTask
{
    private String schemaName;

    @Override
    public String getKind()
    {
        return "LoadedSchemas";
    }

    public void run(AOTaskContext context)
            throws AORestApiException
    {
        final AORestApiPropertyTable reply = context.sendRequest(

                new AORestApiLoadedSchemasRequest()
                        .filter("schema", schemaName)

        );

        if (schemaName != null)
        {
            final AORestApiTableRowEx row = reply.getRowEx("schema", schemaName);

            final AORestApiSchemaStatus status = row != null
                    ? AORestApiSchemaStatus.valueOf(row.getValue("schemaStatus"))
                    : null;

            AOAssertion.assertEquals("loaded-schema", AORestApiSchemaStatus.LOADED, status);
        }
    }

}
