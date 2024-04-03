package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.schema.AORestApiRestoreSchemaBackupMode;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLifeCycle;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLoadStatus;
import ic3.analyticsops.restapi.request.AORestApiRestoreSchemaBackupRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.assertion.AOAssertion;

public class AORestoreSchemaBackupTask extends AOTask
{
    private String schemaName;

    private String timestamp;

    private AORestApiRestoreSchemaBackupMode mode;

    @Override
    public String getKind()
    {
        return "RestoreSchemaBackup";
    }

    public void run(AOTaskContext context)
            throws AORestApiException
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
