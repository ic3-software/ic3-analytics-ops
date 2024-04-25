package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxScriptResult;
import org.jetbrains.annotations.Nullable;

public class AORestApiExecuteMdxRequest extends AORestApiTenantRequest<AORestApiMdxScriptResult>
{
    public AORestApiExecuteMdxRequest()
    {
        super(URL_MDX + "/TidyExecuteMdxScript", AORestApiMdxScriptResult.class);

        addParam("json", String.valueOf(true));

        // Mimic GVI behavior (e.g., cell properties) to make it consistent
        // w/ OpenReport generated expected results.
        addParam("reportingMF", String.valueOf(true));
    }

    public AORestApiExecuteMdxRequest schema(String schemaName)
    {
        return (AORestApiExecuteMdxRequest) addParam("schemaName", schemaName);
    }

    public AORestApiExecuteMdxRequest tidyMaxRowCount(@Nullable Integer tidyMaxRowCount)
    {
        if (tidyMaxRowCount != null)
        {
            return (AORestApiExecuteMdxRequest) addParam("tidyMaxRowCount", String.valueOf(tidyMaxRowCount));
        }
        return this;
    }

    public AORestApiExecuteMdxRequest mdx(String mdx)
    {
        return (AORestApiExecuteMdxRequest) addParam("script", mdx);
    }

}
