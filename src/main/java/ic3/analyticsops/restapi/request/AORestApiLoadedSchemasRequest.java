package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.table.AORestApiPropertyTable;
import org.jetbrains.annotations.Nullable;

public class AORestApiLoadedSchemasRequest extends AORestApiTenantRequest<AORestApiPropertyTable>
{
    public AORestApiLoadedSchemasRequest()
    {
        super(URL_ADMIN + "/LoadedSchemas", AORestApiPropertyTable.class);
    }

    public AORestApiLoadedSchemasRequest filter(String column, @Nullable String value)
    {
        if (value != null)
        {
            return (AORestApiLoadedSchemasRequest) addParam("filter", column + "|CONTAINS|" + value);
        }
        return this;
    }

}
