package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLifeCycle;

public class AORestApiRestoreSchemaSnapshotRequest extends AORestApiTenantRequest<AORestApiSchemaLifeCycle>
{
    public AORestApiRestoreSchemaSnapshotRequest()
    {
        super(URL_ADMIN + "/RestoreOffline", AORestApiSchemaLifeCycle.class);
    }

    public AORestApiRestoreSchemaSnapshotRequest snapshot(String snapshot)
    {
        return (AORestApiRestoreSchemaSnapshotRequest) addParam("name", snapshot);
    }

}
