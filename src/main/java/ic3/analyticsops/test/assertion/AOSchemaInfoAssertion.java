package ic3.analyticsops.test.assertion;

import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaStatus;

public class AOSchemaInfoAssertion extends AOAssertion
{
    private AORestApiSchemaStatus status;

    public void assertOk(AORestApiSchemaStatus actualStatus)
    {
        AOAssertion.assertEquals("schema-info", this.status, actualStatus);
    }

}
