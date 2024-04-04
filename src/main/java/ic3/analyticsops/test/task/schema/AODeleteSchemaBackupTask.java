package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.reply.AORestApiDeleted;
import ic3.analyticsops.restapi.request.AORestApiDeleteSchemaBackupRequest;
import ic3.analyticsops.test.AOAssertion;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.AOTestValidationException;

public class AODeleteSchemaBackupTask extends AOTask
{
    private final String schemaName;

    private final String timestamp;

    public AODeleteSchemaBackupTask()
    {
        // JSON deserialization

        this.schemaName = null;
        this.timestamp = null;
    }

    @Override
    public void validateProps()
            throws AOTestValidationException
    {
        super.validateProps();

        validateNonEmptyField(validateFieldPathPrefix() + "schemaName", schemaName);
        validateNonEmptyField(validateFieldPathPrefix() + "timestamp", timestamp);
    }

    @Override
    public boolean withAssertions()
    {
        return false;
    }

    @Override
    public String getKind()
    {
        return "DeleteSchemaBackup";
    }

    public void run(AOTaskContext context)
            throws AOException
    {
        final AORestApiDeleted reply = context.sendRequest(

                new AORestApiDeleteSchemaBackupRequest()
                        .schemaName(schemaName)
                        // e.g., timestamp: "${LoadSchema.Sales (LiveDemo).info}" to retrieve previous generated backup
                        .timestamp(context.getTaskProperty(timestamp, timestamp))

        );

        AOAssertion.assertTrue("delete-backup", reply.done);
    }

}
