# Test

A test is made of [actors](./Actor.md) that are running a list of [tasks](./Tasks.md). A task can load a schema,
open a dashboard, execute an MDX statement, etc... and can have one or more [assertions](./Assertion.md) to enforce
the expected results.

A test is defined as a `JSON5` file : `JSON5` is an extension of the `JSON` file format that aims to be easier
to write and maintain by hand. Please refer to the [`JSON5`](./JSON5.md) page for more details about the extended
syntax.

## JSON Definition

```typescript
interface Test {

    name: string;

    // E.g., "http://localhost:8282/icCube/api"
    restApiURL?: string;

    authenticator?: Authenticator;

    // Allows for running the test over a period to time (handy for stress-testing).
    duration?: Duration;

    actors: Actor[];
}
```

See also :

- [`JSON5`](./JSON5.md)
- [`Authenticator`](./Authenticator.md)
- [`Duration`](./Duration.md)
- [`Actor`](./Actor.md)

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../src/main/java/ic3/analyticsops/test/AOTest.java).

_