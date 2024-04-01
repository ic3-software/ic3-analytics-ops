package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.table.AORestApiServerStatusTable;

public class AORestApiServerStatusRequest extends AORestApiRequest<AORestApiServerStatusTable>
{
    public AORestApiServerStatusRequest()
    {
        super(URL_ADMIN + "/ServerStatus", AORestApiServerStatusTable.class);
    }
}
