package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.AORestApiDeleted;
import ic3.analyticsops.restapi.request.AORestApiDeleteSchemaBackupRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.assertion.AOAssertion;

public class AODeleteSchemaBackupTask extends AOTask
{
    private String schemaName;

    private String timestamp;

    @Override
    public String getKind()
    {
        return "DeleteSchemaBackup";
    }

    public void run(AOTaskContext context)
            throws AORestApiException
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
