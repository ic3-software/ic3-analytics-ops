package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.table.AORestApiTable;

public class AORestApiClearAuthDataCacheRequest extends AORestApiTenantRequest<AORestApiTable>
{
    public AORestApiClearAuthDataCacheRequest()
    {
        super(URL_ADMIN + "/ClearAuthServiceExternalData", AORestApiTable.class);
    }
}
