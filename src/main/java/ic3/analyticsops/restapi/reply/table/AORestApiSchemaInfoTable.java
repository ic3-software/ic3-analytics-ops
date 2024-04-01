package ic3.analyticsops.restapi.reply.table;

import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaStatus;

public class AORestApiSchemaInfoTable extends AORestApiPropertyTable
{
    public AORestApiSchemaStatus getStatus()
    {
        final String status = getValue("status");
        return AORestApiSchemaStatus.valueOf(status);
    }

}
