package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.schema.AORestApiRestoreSchemaBackupMode;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLifeCycle;

public class AORestApiRestoreSchemaBackupRequest extends AORestApiTenantRequest<AORestApiSchemaLifeCycle>
{
    public AORestApiRestoreSchemaBackupRequest()
    {
        super(URL_ADMIN + "/RestoreBackup", AORestApiSchemaLifeCycle.class);
    }

    public AORestApiRestoreSchemaBackupRequest schemaName(String schemaName)
    {
        return (AORestApiRestoreSchemaBackupRequest) addParam("schemaName", schemaName);
    }

    public AORestApiRestoreSchemaBackupRequest timestamp(String timestamp)
    {
        return (AORestApiRestoreSchemaBackupRequest) addParam("timestamp", timestamp);
    }

    public AORestApiRestoreSchemaBackupRequest mode(AORestApiRestoreSchemaBackupMode mode)
    {
        return (AORestApiRestoreSchemaBackupRequest) addParam("mode", mode.name());
    }

}
