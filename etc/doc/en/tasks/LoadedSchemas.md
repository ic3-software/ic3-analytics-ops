# LoadedSchemas

Return some information about the icCube server (requires an icCube server v8.4.10 for the version info.).

By default, this task is asserting the schema is loaded.

## JSON Definition

```typescript
interface LoadedSchemasTask extends Task {
    
    schemaName: string;
    
}
```

See also :

- [`JSON5`](../JSON5.md)
- [`Tasks`](../Tasks.md)
- [`Task`](../Task.md)

## Example

```json5
{
  action: "LoadedSchemas",
  schemaName: "Sales"
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../../src/main/java/ic3/analyticsops/test/task/schema/AOLoadedSchemasTask.java).

_
