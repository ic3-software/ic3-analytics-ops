package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLifeCycle;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLoadStatus;
import ic3.analyticsops.restapi.request.AORestApiUnloadSchemaRequest;
import ic3.analyticsops.test.*;

public class AOUnloadSchemaTask extends AOTask
{
    private final String schemaName;

    protected AOUnloadSchemaTask()
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
        return "UnloadSchema";
    }

    @Override
    public AOAssertionMode getAssertionsMode()
    {
        return AOAssertionMode.NONE;
    }

    public void run(AOTaskContext context)
            throws AOException
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
