# LoadTestConfiguration

A load-test is a special kind of test that applies a simulated workload onto an icCube installation to see how it
performs. You can refer to this [page](https://grafana.com/load-testing/) for a nice description and introduction
of load-testing written by GrafanaLabs.

A load-test is made of several profiles attached to an actor. Each profile is basically defining how many actors
are used for a period of time (steady-state stage) and how fast they are started (ramp-up stage). Remember that
each actor is run in its own thread of control. This way you can simulate several concurrent users using icCube.
Each actor can define some performance targets to ensure the server is behaving as expected under the simulated
workload.

## JSON Definition

```typescript
interface LoadTestConfiguration {

    // Load test profiles for each actor.
    actors: LoadTestActorConfiguration[];

    // Optional max. (testing) system load before an error is generated (a value between 0 and 1).
    failAtCpuLoad?: number;

    // The CPU load is checked every 'cpuLoadTick'. Defaulted to 1 sec.
    cpuLoadTick?: Duration;

    // The statistics are reported every 'statsTick'. Defaulted to 1 sec.
    statsTick?: Duration;

}

interface LoadTestActorConfiguration {

    actor: string;

    // The number of actors (threads) in the steady-state stage.
    count: number;

    // An optional initial delay before the ramp-up stage is starting.
    delay?: Duration;

    // How long is the ramp-up stage : each actor is created every 'ramp-up / count' millis.
    rampUp: Duration;

    // How long is the steady-state stage : all actors are running their tasks for this period of time.
    steadyState: Duration;

    // How long is the ramp-down stage : each actor is stopped every 'ramp-down / count' millis.
    rampDown: Duration;

}
```

See also :

- [`JSON5`](./JSON5.md)
- [`Duration`](./Duration.md)
- [`Test`](./Test.md)

## Example

The following definition is simulating an average-load test where 10 actors are executing some MDX statements for
a period of 60 seconds after a ramp-up period of 20 seconds (a new actor is started every 2 seconds). At the same time,
the test is asserting that the system load of the testing system is not exceeding 0.5 as a way to ensure it is not
saturated.

```json5
{
  load: {
    failAtCpuLoad: 0.5,
    actors: [
      {
        actor: "MDX Player",
        count: 10,
        rampUp: "PT20s",
        steadyState: "PT60s",
        rampDown: "PT10s"
      }
    ]
  }
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../src/main/java/ic3/analyticsops/test/load/AOLoadTestConfiguration.java).

_