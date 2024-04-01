package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.table.AORestApiTable;

public class AORestApiClearResultCacheRequest extends AORestApiRequest<AORestApiTable>
{
    public AORestApiClearResultCacheRequest()
    {
        super(URL_ADMIN + "/ClearResultCache", AORestApiTable.class);
    }
}
