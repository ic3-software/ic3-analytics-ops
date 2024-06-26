package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaStatus;
import ic3.analyticsops.restapi.reply.table.AORestApiPropertyTable;
import ic3.analyticsops.restapi.reply.table.AORestApiTableRowEx;
import ic3.analyticsops.restapi.request.AORestApiLoadedSchemasRequest;
import ic3.analyticsops.test.*;

public class AOLoadedSchemasTask extends AOTask
{
    private final String schemaName;

    protected AOLoadedSchemasTask()
    {
        // JSON deserialization

        this.schemaName = null;
    }

    @Override
    public void validateProps()
            throws AOTestValidationException
    {
        super.validateProps();

        validateNonEmptyField(validateFieldPathPrefix() + "schemaName", schemaName);
    }

    @Override
    public String getKind()
    {
        return "LoadedSchemas";
    }

    @Override
    public AOAssertionMode getAssertionsMode()
    {
        return AOAssertionMode.NONE;
    }

    public void run(AOTaskContext context)
            throws AOException
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
