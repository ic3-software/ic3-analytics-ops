# LoadSchema

Fully (or incrementally) load a schema.

By default, this task asserts the schema is successfully loaded.

When generating a backup, this task is creating the following **property** :

    ${LoadSchema." + reply.schemaName + ".info}

that can be used by a following task (see the [example](#example) section below).

Note this task is using the [LoadSchema](https://doc.iccube.com/?ic3topic=server.api.LoadSchema) REST API request.

## JSON Definition

```typescript
interface LoadSchemaTask extends Task {

    schemaFile: string;

    incrLoad?: boolean;

    // Restore the MDX result cache once the incremental load has been performed if required. 
    // That is, existing cached requests are re-executed and cached.
    keepMdxResultCache?: boolean;

    forceBackup?: boolean;

}
```

See also :

- [`JSON5`](../JSON5.md)
- [`Tasks`](../Tasks.md)
- [`Task`](../Task.md)

## Example

Load (or reload) the schema `Sales` :

```json5
{
  action: "LoadSchema",
  schemaFile: "Sales.icc-schema"
}
```

Load the schema `Sales (LiveDemo)` and generate a backup and restore this backup using a generated property
to identify the generated backup :

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
    }
  ]
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../../src/main/java/ic3/analyticsops/test/task/schema/AOLoadSchemaTask.java).

_
