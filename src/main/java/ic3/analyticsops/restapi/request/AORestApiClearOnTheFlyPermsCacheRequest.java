package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.table.AORestApiTable;

public class AORestApiClearOnTheFlyPermsCacheRequest extends AORestApiRequest<AORestApiTable>
{
    public AORestApiClearOnTheFlyPermsCacheRequest()
    {
        super(URL_ADMIN + "/DeleteAllPerms", AORestApiTable.class);
    }
}
