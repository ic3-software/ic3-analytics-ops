package ic3.analyticsops.test.assertion;

import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaStatus;
import ic3.analyticsops.test.AOAssertion;
import ic3.analyticsops.test.AOTestValidationException;

public class AOSchemaInfoAssertion extends AOAssertion
{
    private final AORestApiSchemaStatus status;

    protected AOSchemaInfoAssertion()
    {
        // JSON deserialization

        this.status = null;
    }

    @Override
    public void validate()
            throws AOTestValidationException
    {
        super.validate();

        validateNonEmptyField(validateFieldPathPrefix() + "status", status);
    }

    public void assertOk(AORestApiSchemaStatus actualStatus)
    {
        AOAssertion.assertEquals("schema-info", this.status, actualStatus);
    }

}
