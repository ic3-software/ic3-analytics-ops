package ic3.analyticsops.test.task.schema;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLifeCycle;
import ic3.analyticsops.restapi.reply.schema.AORestApiSchemaLoadStatus;
import ic3.analyticsops.restapi.request.AORestApiRestoreSchemaSnapshotRequest;
import ic3.analyticsops.test.*;

public class AORestoreSchemaSnapshotTask extends AOTask
{
    private final String snapshot;

    protected AORestoreSchemaSnapshotTask()
    {
        // JSON deserialization

        this.snapshot = null;
    }

    @Override
    public void validateProps()
            throws AOTestValidationException
    {
        super.validateProps();

        validateNonEmptyField(validateFieldPathPrefix() + "snapshot", snapshot);
    }

    @Override
    public String getKind()
    {
        return "RestoreSchemaSnapshot";
    }

    @Override
    public AOAssertionMode getAssertionsMode()
    {
        return AOAssertionMode.NONE;
    }

    public void run(AOTaskContext context)
            throws AOException
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
