# DeleteSchemaBackup

Delete a schema backup.

By default, this task asserts the backup is successfully deleted.

Note this task is using the [DeleteBackup](https://doc.iccube.com/?ic3topic=server.api.DeleteBackup) REST API request.

## JSON Definition

```typescript
interface DeleteSchemaBackupTask extends Task {

    schemaFile: string;

    timestamp: string;

}
```

See also :

- [`JSON5`](../JSON5.md)
- [`Tasks`](../Tasks.md)
- [`Task`](../Task.md)

## Example

Delete a previously generated schema backup using a property :

```json5
{
  tasks: [
    {
      action: "LoadSchema",
      schemaFile: "Sales__LiveDemo_.icc-schema",
      forceBackup: true
    },
    {
      action: "RestoreSchemaBackup",
      schemaName: "Sales (LiveDemo)",
      timestamp: "${LoadSchema.Sales (LiveDemo).info}"
    },
    {
      action: "DeleteSchemaBackup",
      schemaName: "Sales (LiveDemo)",
      timestamp: "${LoadSchema.Sales (LiveDemo).info}"
    }
  ]
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../../src/main/java/ic3/analyticsops/test/task/schema/AODeleteSchemaBackupTask.java).

_
