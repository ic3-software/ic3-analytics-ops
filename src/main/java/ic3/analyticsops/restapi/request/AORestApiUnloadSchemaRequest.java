package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLifeCycle;

public class AORestApiUnloadSchemaRequest extends AORestApiTenantRequest<AORestApiSchemaLifeCycle>
{
    public AORestApiUnloadSchemaRequest()
    {
        super(URL_ADMIN + "/UnloadSchema", AORestApiSchemaLifeCycle.class);
    }

    public AORestApiUnloadSchemaRequest schemaName(String schemaName)
    {
        return (AORestApiUnloadSchemaRequest) addParam("schemaName", schemaName);
    }

}
