package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxScriptResult;

public class AORestApiExecuteMdxRequest extends AORestApiTenantRequest<AORestApiMdxScriptResult>
{
    public AORestApiExecuteMdxRequest()
    {
        super(URL_MDX + "/TidyExecuteMdxScript", AORestApiMdxScriptResult.class);

        addParam("json", String.valueOf(true));
    }

    public AORestApiExecuteMdxRequest schema(String schemaName)
    {
        return (AORestApiExecuteMdxRequest) addParam("schemaName", schemaName);
    }

    public AORestApiExecuteMdxRequest mdx(String mdx)
    {
        return (AORestApiExecuteMdxRequest) addParam("script", mdx);
    }

}
