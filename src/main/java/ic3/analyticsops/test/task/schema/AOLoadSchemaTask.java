package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLifeCycle;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLoadStatus;
import ic3.analyticsops.restapi.request.AORestApiLoadSchemaRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.assertion.AOAssertion;

public class AOLoadSchemaTask extends AOTask
{
    private String schemaFile;

    private boolean incrLoad;

    private boolean keepMdxResultCache;

    private boolean forceBackup;

    @Override
    public String getKind()
    {
        return "LoadSchema";
    }

    public void run(AOTaskContext context)
            throws AORestApiException
    {
        final AORestApiSchemaLifeCycle reply = context.sendRequest(

                new AORestApiLoadSchemaRequest()
                        .schemaFile(schemaFile)
                        .incrLoad(incrLoad)
                        .keepMdxResultCache(keepMdxResultCache)
                        .forceBackup(forceBackup)

        );

        final AORestApiSchemaLoadStatus actualStatus = reply.status;

        AOAssertion.assertEquals("load-schema-status", AORestApiSchemaLoadStatus.LOADED, actualStatus);
        AOAssertion.assertNull("load-schema-error", reply.errors);
    }

}
