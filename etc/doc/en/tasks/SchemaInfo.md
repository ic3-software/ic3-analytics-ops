# SchemaInfo

Retrieve some information about a deployed schema.

Note this task is using the [SchemaInfo](https://doc.iccube.com/?ic3topic=server.api.SchemaInfo) REST API request.

## JSON Definition

```typescript
interface SchemaInfoTask extends Task<SchemaInfoAssertion> {

    schemaFile: string;

}

interface SchemaInfoAssertion extends Assertion {

    status : SchemaStatus;
    
}
```

See also :

- [`JSON5`](../JSON5.md)
- [`SchemaStatus`](./SchemaStatus.md)
- [`Tasks`](../Tasks.md)
- [`Task`](../Task.md)

## Example

Ensure the schema `Sales` is currently loaded :

```json5
{
  action: "SchemaInfo",
  schemaFile: "Sales.icc-schema",
  assertions: [
    {
      status: "LOADED"
    }
  ]
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../../src/main/java/ic3/analyticsops/test/task/schema/AOSchemaInfoTask.java).

_
