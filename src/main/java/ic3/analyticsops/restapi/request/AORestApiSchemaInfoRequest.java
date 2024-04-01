package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.table.AORestApiSchemaInfoTable;

public class AORestApiSchemaInfoRequest extends AORestApiTenantRequest<AORestApiSchemaInfoTable>
{
    public AORestApiSchemaInfoRequest()
    {
        super(URL_ADMIN + "/SchemaInfo", AORestApiSchemaInfoTable.class);
    }

    public AORestApiSchemaInfoRequest schemaName(String schema)
    {
        return (AORestApiSchemaInfoRequest) addParam("schemaName", schema);
    }

    /**
     * Use that one to ensure to locate unloaded schema as well.
     */
    public AORestApiSchemaInfoRequest schemaFactoryFile(String file)
    {
        return (AORestApiSchemaInfoRequest) addParam("schemaFile", file);
    }

}
