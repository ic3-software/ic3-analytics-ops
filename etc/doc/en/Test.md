# Test

A test is made of [actors](./Actor.md) that are running a list of [tasks](./Tasks.md). A task can load a schema,
open a dashboard, execute an MDX statement, etc... and can have one or more [assertions](./Assertion.md) to enforce
the expected results.

A test is defined as a `JSON5` file : `JSON5` is an extension of the `JSON` file format that aims to be easier
to write and maintain by hand. Please refer to the [`JSON5`](./JSON5.md) page for more details about the extended
syntax.

A load-test is a special kind of test that applies a simulated workload onto an icCube installation to see how
it performs. Refer to the [LoadTestConfiguration](./LoadTestConfiguration.md) page for more details.

## JSON Definition

```typescript
interface Test {

    name: string;

    // Possibly overridden in each actor.
    // E.g., "http://localhost:8282/icCube/api"
    restApiURL?: string;

    // Possibly overridden in each actor.
    authenticator?: Authenticator;

    // See ExecuteMdxAssertion : allows for comparing ExectueMdx results using different security profiles.
    elevatedAuthenticator?: Authenticator;

    // REST API request timeout.
    // Possibly overridden in each actor.
    // Default: 30s.
    timeout? : Duration;
    
    chrome?: ChromeConfiguration;
    
    // Allows for running the test over a period to time.
    // Cannot be used when 'load' has been defined.
    duration?: Duration;

    actors: Actor[];

    // An optional configuration to make this test running as a load-test : stress, ...
    load?: LoadTestConfiguration;

}
```

See also :

- [`JSON5`](./JSON5.md)
- [`Authenticator`](./Authenticator.md)
- [`ChromeConfiguration`](./ChromeConfiguration.md)
- [`Duration`](./Duration.md)
- [`Actor`](./Actor.md)
- [`LoadTestConfiguration`](./LoadTestConfiguration.md)

## Example

A test with a single actor performing some non-regression against the restored `CRM` schema :

```json5
{
  name: "Non-Regression",
  restApiURL: "http://localhost:8282/icCube/api",
  authenticator: {
    user: "${analytics.ops.user}",
    password: "${analytics.ops.password}"
  },
  actors: [
    {
      name: "CRM",
      tasks: [
        {
          action: "RestoreSchemaSnapshot",
          snapshot: "CRM.icc-schema.1326727789266"
        },
        {
          action: "MDXes",
          data: "data/CRM",
          schema: "CRM"
        }
      ]
    }
  ]
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../src/main/java/ic3/analyticsops/test/AOTest.java).

_