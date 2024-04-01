package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLifeCycle;

public class AORestApiLoadSchemaRequest extends AORestApiTenantRequest<AORestApiSchemaLifeCycle>
{
    public AORestApiLoadSchemaRequest()
    {
        super(URL_ADMIN + "/LoadSchema", AORestApiSchemaLifeCycle.class);
    }

    public AORestApiLoadSchemaRequest schemaFile(String file)
    {
        return (AORestApiLoadSchemaRequest) addParam("schemaFile", file);
    }

    public AORestApiLoadSchemaRequest incrLoad(boolean incrLoad)
    {
        return (AORestApiLoadSchemaRequest) addParam("incrLoad", String.valueOf(incrLoad));
    }

    public AORestApiLoadSchemaRequest keepMdxResultCache(boolean keepMdxResultCache)
    {
        return (AORestApiLoadSchemaRequest) addParam("keepMdxResultCache", String.valueOf(keepMdxResultCache));
    }

    public AORestApiLoadSchemaRequest forceBackup(boolean forceBackup)
    {
        return (AORestApiLoadSchemaRequest) addParam("forceBackup", String.valueOf(forceBackup));
    }

}
