package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.reply.schema.AORestApiRestoreSchemaBackupMode;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLifeCycle;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLoadStatus;
import ic3.analyticsops.restapi.request.AORestApiRestoreSchemaBackupRequest;
import ic3.analyticsops.test.AOAssertion;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.AOTestValidationException;
import org.jetbrains.annotations.Nullable;

public class AORestoreSchemaBackupTask extends AOTask
{
    private final String schemaName;

    private final String timestamp;

    @Nullable
    private final AORestApiRestoreSchemaBackupMode mode;

    protected AORestoreSchemaBackupTask()
    {
        // JSON deserialization

        this.schemaName = null;
        this.timestamp = null;
        this.mode = null;
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
    public String getKind()
    {
        return "RestoreSchemaBackup";
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

                new AORestApiRestoreSchemaBackupRequest()
                        .schemaName(schemaName)
                        // e.g., timestamp: "${LoadSchema.Sales (LiveDemo).info}" to retrieve previous generated backup
                        .timestamp(context.getTaskProperty(timestamp, timestamp))
                        .mode(mode != null ? mode : AORestApiRestoreSchemaBackupMode.FULL)

        );

        final AORestApiSchemaLoadStatus actualStatus = reply.status;

        AOAssertion.assertEquals("restore-schema-backup-status", AORestApiSchemaLoadStatus.LOADED, actualStatus);
        AOAssertion.assertNull("restore-schema-backup-error", reply.errors);
    }

}
