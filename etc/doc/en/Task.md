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

}

interface Assertion {

    // Refer to each concrete implementation.

}
```

See also :

- [`JSON5`](./JSON5.md)
- [`Pause`](./Pause.md)
- [`Assertion`](./Assertion.md)
- [`Actor`](./Actor.md)
- [`Test`](./Test.md)
- [`Tasks`](./Tasks.md)

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../src/main/java/ic3/analyticsops/test/AOTask.java).

_
