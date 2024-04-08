package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLifeCycle;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLoadStatus;
import ic3.analyticsops.restapi.request.AORestApiLoadSchemaRequest;
import ic3.analyticsops.test.AOAssertion;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.AOTestValidationException;
import org.jetbrains.annotations.Nullable;

public class AOLoadSchemaTask extends AOTask
{
    private final String schemaFile;

    @Nullable
    private final Boolean incrLoad;

    @Nullable
    private final Boolean keepMdxResultCache;

    @Nullable
    private final Boolean forceBackup;

    protected AOLoadSchemaTask()
    {
        // JSON deserialization

        this.schemaFile = null;
        this.incrLoad = null;
        this.keepMdxResultCache = null;
        this.forceBackup = null;
    }

    @Override
    public void validateProps()
            throws AOTestValidationException
    {
        super.validateProps();

        validateNonEmptyField(validateFieldPathPrefix() + "schemaFile", schemaFile);
    }

    @Override
    public String getKind()
    {
        return "LoadSchema";
    }

    @Override
    public boolean withAssertions()
    {
        return false;
    }

    public void run(AOTaskContext context)
            throws AOException
    {
        final AORestApiSchemaLifeCycle reply = context.sendRequest(

                new AORestApiLoadSchemaRequest()
                        .schemaFile(schemaFile)
                        .incrLoad(incrLoad != null ? incrLoad : false)
                        .keepMdxResultCache(keepMdxResultCache != null ? keepMdxResultCache : false)
                        .forceBackup(forceBackup != null ? forceBackup : false)

        );

        final AORestApiSchemaLoadStatus actualStatus = reply.status;

        AOAssertion.assertEquals("load-schema-status", AORestApiSchemaLoadStatus.LOADED, actualStatus);
        AOAssertion.assertNull("load-schema-error", reply.errors);

        if (forceBackup)
        {
            context.setTaskProperty("${LoadSchema." + reply.schemaName + ".info}", reply.info);
        }
    }

}
