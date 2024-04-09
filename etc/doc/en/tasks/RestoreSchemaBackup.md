# RestoreSchemaBackup

Load a schema by restoring a backup.

By default, this task asserts the schema is successfully loaded.

Note this task is using the [RestoreBackup](https://doc.iccube.com/?ic3topic=server.api.RestoreBackup) REST API request.

## JSON Definition

```typescript
interface RestoreSchemaBackupTask extends Task {

    schemaFile: string;
    
    timestamp: string;
    
    mode? : "INITIAL_LOAD" | "FULL";
}
```

See also :

- [`JSON5`](../JSON5.md)
- [`Tasks`](../Tasks.md)
- [`Task`](../Task.md)

## Example

Restore the backup `2024-04-03 12-56-34 517 UTC` of the schema `Sales` :

```json5
{
  action: "RestoreSchemaBackup",
  schemaName: "Sales",
  timestamp: "2024-04-03 12-56-34 517 UTC"
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../../src/main/java/ic3/analyticsops/test/task/schema/AORestoreSchemaBackupTask.java).

_
