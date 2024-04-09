# RestoreSchemaSnapshot

Load a schema by restoring a snapshot.

By default, this task asserts the schema is successfully loaded.

Note this task is using the [RestoreOffline](https://doc.iccube.com/?ic3topic=server.api.RestoreOffline) REST API
request.

## JSON Definition

```typescript
interface AORestoreSchemaSnapshotTask extends Task {

    snapshot: string;

}
```

See also :

- [`JSON5`](../JSON5.md)
- [`Tasks`](../Tasks.md)
- [`Task`](../Task.md)

## Example

Restore the snapshot `Sales.icc-schema.2024_03_18_15h57m09.1710773829600` of the schema `Sales` :

```json5
{
  action: "RestoreSchemaSnapshot",
  snapshot: "Sales.icc-schema.2024_03_18_15h57m09.1710773829600"
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../../src/main/java/ic3/analyticsops/test/task/schema/AORestoreSchemaSnapshotTask.java).

_
