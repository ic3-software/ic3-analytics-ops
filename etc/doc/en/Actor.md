# Actor

An actor is defining a list of [tasks](./Tasks.md) to run in sequence.

A [task](./Task.md) can load a schema, open a dashboard, execute an MDX statement, etc... and can have one or more
[assertions](./Assertion.md) to enforce the expected results.

Each actor **runs in its own thread** of control.

## JSON Definition

```typescript
interface Actor {
    
    name : string;
    
    // Default : true
    active? : boolean;
    
    // Overriding the one defined at test level : handy for testing several remote (scaling-up) containers.
    // E.g., "http://localhost:8282/icCube/api"
    restApiURL? : string;

    // Overriding the one defined at test level : handy for testing several security profiles.
    authenticator?: Authenticator;

    // Overriding the one defined at test level.
    timeout?: Duration;
    
    // Writes to the log the actual JSON returned by the server for each REST API request.
    // Default : false
    dumpJson? : boolean;

    // Writes to the log a pretty-print version of the payload of the JSON replies.
    // Default : false
    dumpResult? : boolean;

    tasks: Task[];

}
```

See also :

- [`JSON5`](./JSON5.md)
- [`Authenticator`](./Authenticator.md)
- [`Task`](./Task.md)
- [`Test`](./Test.md)

## Example

An actor printing a single report :

```json5
{
  name: "Dashboard Printer",
  tasks: [
    {
      action: "PrintReport",
      reportPath: "shared:/ic3-analytics-ops",
      pageSize: "A4"
    }
  ]
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../src/main/java/ic3/analyticsops/test/AOActor.java).

_