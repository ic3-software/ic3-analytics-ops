package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaStatus;
import ic3.analyticsops.restapi.reply.table.AORestApiSchemaInfoTable;
import ic3.analyticsops.restapi.request.AORestApiSchemaInfoRequest;
import ic3.analyticsops.test.AOAssertionMode;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.AOTestValidationException;
import ic3.analyticsops.test.assertion.AOSchemaInfoAssertion;

import java.util.List;

public class AOSchemaInfoTask extends AOTask<AOSchemaInfoAssertion>
{
    private final String schemaFile;

    protected AOSchemaInfoTask()
    {
        // JSON deserialization

        this.schemaFile = null;
    }

    @Override
    public void validateProps()
            throws AOTestValidationException
    {
        super.validateProps();

        validateNonEmptyField(validateFieldPathPrefix() + "schemaFile", schemaFile);
    }

    @Override
    public String getKind()
    {
        return "SchemaInfo";
    }

    @Override
    public AOAssertionMode getAssertionsMode()
    {
        return AOAssertionMode.MANDATORY;
    }

    @Override
    public void run(AOTaskContext context)
            throws AOException
    {
        final AORestApiSchemaInfoTable reply = context.sendRequest(

                new AORestApiSchemaInfoRequest()
                        .schemaFactoryFile(schemaFile)

        );

        // If the schema is unknown an exception has been thrown.

        final AORestApiSchemaStatus actualStatus = reply.getStatus();

        final List<AOSchemaInfoAssertion> assertions = getAssertions();

        for (AOSchemaInfoAssertion assertion : assertions /* validated by now */)
        {
            assertion.assertOk(actualStatus);
        }
    }
}
