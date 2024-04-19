# Task

A test is made of [actors](./Actor.md) that are running a list of [tasks](./Tasks.md). A task can load a schema,
open a dashboard, execute an MDX statement, etc... and can have one or more [assertions](./Assertion.md) to enforce
the expected results.

A task can **pass information** to following tasks; for example, the `LoadSchema` with `forceBackup=true` will
generate a property containing the timestamp of the generated backup. This property can then be used by a
`RestoreSchemaBackup` later. Refer to each task for a description of each generated property.

Refer to this [page](./Tasks.md) for a list of al the available tasks.

## JSON Definition

```typescript
interface Task<ASSERTION extends Assertion> {

    // When null its kind will be used instead.
    name?: string;

    // Writes to the log the actual JSON returned by the server for each REST API request.
    // Overriding the one defined at actor level.
    dumpJson?: boolean;

    // Writes to the log a pretty-print version of the payload of the JSON replies.
    // Overriding the one defined at actor level.
    dumpResult?: boolean;

    // An optional pause applied after the processing of the task.
    pause?: Pause;

    assertions?: ASSERTION[];

    performanceTargets?: PerformanceTarget[];

}

interface Assertion {

    // Refer to each concrete implementation.

}

interface PerformanceTarget {

    // Asserted after each run of the task.
    durationMax?: Duration;

    // Asserted at the end of the test.
    durationAverageEnd?: Duration;

}
```

See also :

- [`JSON5`](./JSON5.md)
- [`Pause`](./Pause.md)
- [`Duration`](./Duration.md)
- [`Assertion`](./Assertion.md)
- [`Actor`](./Actor.md)
- [`Test`](./Test.md)
- [`Tasks`](./Tasks.md)

## Example

A single actor executing a bunch of MDX queries against the restored `Sales` schema and ensuring that all those
queries are always executed in less than 10 seconds and eventually their average duration is less than 5 seconds :

```json5
{
  actors: [
    {
      name: "MDX Player",
      tasks: [
        {
          action: "MDXes",
          data: "data/sales",
          schema: "Sales",
          performanceTargets: {
            durationMax: "PT10s",
            durationAverageEnd: "PT5s"
          }
        }
      ]
    }
  ]
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../src/main/java/ic3/analyticsops/test/AOTask.java).

_
