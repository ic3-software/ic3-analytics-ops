# ExecuteMdx

Execute an MDX statement and assert its result using another equivalent MDX statement.

This task is **not using the server MDX result cache**.

Note this task is using the [TidyExecuteMdxScript](https://doc.iccube.com/?ic3topic=server.api.TidyExecuteMdxScript)
REST API request.

## JSON Definition

```typescript
interface ExecuteMdxTask extends Task<ExecuteMdxAssertion> {

    schema: string;

    statemnt: string;

}

interface ExecuteMdxAssertion extends Assertion {

    // The both results are strictly the same.
    equals?: {
        statement: string;
    };

    // Only the content of the cells are the same : member columns, name of the columns are ignored.
    cellEquals?: {
        stamtent: string;
    };

    // The statement must generate an expected error.
    statementOnError?: {
        errorCode: string;
    };

    // The statement must generate a single cell with an expected error.
    // Assuming a single-cell result.
    cellOnError?: {
        errorCode: string;
    };

}
```

See also :

- [`JSON5`](../JSON5.md)
- [`Tasks`](../Tasks.md)
- [`Task`](../Task.md)

## Example

Ensure the both statements are generating exactly the same result :

```json5
{
  action: "MDX",
  schema: "Sales",
  statement: "select TopCount( [Customers].[Geography].[Region], 3, [Measures].[Count] ) on 0 from [Sales]",
  assertions: [
    {
      equals: {
        statement: "select Head( Order( [Customers].[Geography].[Region], [Measures].[Count], BDESC ), 3) on 0 from [Sales]"
      }
    }
  ]
}
```

Ensure the both statements are generating the same cells :

```json5
{
  action: "MDX",
  schema: "Sales",
  statement: "select from [Sales] filterby [Customers].[Geography].[Region].[Europe]",
  assertions: [
    {
      cellEquals: {
        statement: "select [Customers].[Geography].[Region].[Europe] on 0 from [Sales]"
      }
    }
  ]
}
```

Ensure the statement is generating an error :

```json5
{
  action: "MDX",
  schema: "Sales",
  statement: "select from Sales where x",
  assertions: [
    {
      statementOnError: {
        errorCode: "OLAP_UNKNOWN_DIMENSION_HIERARCHY"
      }
    }
  ]
}
```

Ensure the statement is generating a cell on error :

```json5
{
  action: "MDX",
  schema: "Sales",
  statement: "with x as Error('ouch') select from Sales where x",
  assertions: [
    {
      cellOnError: {
        errorCode: "OLAP_MDX_ERROR_FUNCTION"
      }
    }
  ]
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../../src/main/java/ic3/analyticsops/test/task/mdx/AOExecuteMdxTask.java).

_
