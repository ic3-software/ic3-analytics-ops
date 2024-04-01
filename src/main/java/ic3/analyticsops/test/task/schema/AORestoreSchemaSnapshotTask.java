package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLifeCycle;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLoadStatus;
import ic3.analyticsops.restapi.request.AORestApiRestoreSchemaSnapshotRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.assertion.AOAssertion;

public class AORestoreSchemaSnapshotTask extends AOTask
{
    private String snapshot;

    @Override
    public String getKind()
    {
        return "RestoreSchemaSnapshot";
    }

    public void run(AOTaskContext context)
            throws AORestApiException
    {
        final AORestApiSchemaLifeCycle reply = context.sendRequest(

                new AORestApiRestoreSchemaSnapshotRequest()
                        .snapshot(snapshot)

        );

        final AORestApiSchemaLoadStatus actualStatus = reply.status;

        AOAssertion.assertEquals("restore-schema-snapshot-status", AORestApiSchemaLoadStatus.LOADED, actualStatus);
        AOAssertion.assertNull("restore-schema-snapshot-error", reply.errors);
    }

}
