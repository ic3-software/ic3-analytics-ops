# GenerateMDXes

Generates the expected results of the task [`MDXes`](./MDXes.md).

This task is **not using the server MDX result cache**.

The files containing the MDX statements to execute are defined using the `data` field. For example, the following
configuration :

    data: "data/Sales"

is using the files `Sales*` in the `data` folder. This folder is **relative the parent folder** of the `JSON5` file
containing the test definition being run :

    tests/
        data/
            sales.0.mdx.txt
            sales.1.mdx.txt
            sales.2.mdx.txt
    smoke.test.json5

The generated results are compressed but, you can keep them uncompressed if you like :

    tests/
        data/
            sales.0.mdx.txt
            sales.0.mdx.json.zip      : generated
            sales.1.mdx.txt
            sales.1.mdx.json.zip      : generated
            sales.2.mdx.txt
            sales.2.mdx.json.zip      : generated
    smoke.test.json5

Note this task is using the [TidyExecuteMdxScript](https://doc.iccube.com/?ic3topic=server.api.TidyExecuteMdxScript)
REST API request.

## JSON Definition

```typescript
interface MDXesTask extends Task {

    schema: string;

    // E.g., data/Sales is using the files 'Sales-N.mdx.txt' into the 'data' folder.
    data: string;

}
```

See also :

- [`JSON5`](../JSON5.md)
- [`Tasks`](../Tasks.md)
- [`Task`](../Task.md)

## Example

Execute the statements in the `data` folder and generate their results :

```json5
{
  action: "GenerateMDXes",
  data: "data/sales",
  schema: "Sales",
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../../src/main/java/ic3/analyticsops/test/task/mdx/AOGenerateMDXesTask.java).

_
