# UnloadSchema

Unload a schema.

By default, this task asserts the schema is properly unloaded (or nothing was required).

## JSON Definition

```typescript
interface UnloadSchemaTask extends Task {

    schemaName: string;

}
```

See also :

- [`JSON5`](../JSON5.md)
- [`Tasks`](../Tasks.md)
- [`Task`](../Task.md)

## Example

Unload the previously loaded `Sales` schema :

```json5
{
  action: "UnloadSchema",
  schemaName: "Sales"
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../../src/main/java/ic3/analyticsops/test/task/schema/AOUnloadSchemaTask.java).

_
