package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.AORestApiDeleted;

public class AORestApiDeleteSchemaBackupRequest extends AORestApiTenantRequest<AORestApiDeleted>
{
    public AORestApiDeleteSchemaBackupRequest()
    {
        super(URL_ADMIN + "/DeleteBackup", AORestApiDeleted.class);
    }

    public AORestApiDeleteSchemaBackupRequest schemaName(String schemaName)
    {
        return (AORestApiDeleteSchemaBackupRequest) addParam("schemaName", schemaName);
    }

    public AORestApiDeleteSchemaBackupRequest timestamp(String timestamp)
    {
        return (AORestApiDeleteSchemaBackupRequest) addParam("timestamp", timestamp);
    }

}
