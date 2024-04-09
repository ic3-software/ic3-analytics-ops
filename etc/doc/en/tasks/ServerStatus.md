# ServerStatus

Returns some information about the icCube server (requires an icCube server v8.4.10 for the version info.).

By default, this task is asserting the server is returning some information. When defined, the assertions
allow for testing the versions of the server and/or the installed reporting application : exact match.

Note this task is using the [ServerStatus](https://doc.iccube.com/?ic3topic=server.api.ServerStatus) REST API request.

## JSON Definition

```typescript
interface ServerStatusTask extends Task<ServerStatusAssertion> {
}

interface ServerStatusAssertion extends Assertion {

    // Full version string (i.e., with timestamp).
    serverVersionEx?: string;

    // E.g., 8.4.10
    serverVersion?: string;

    // Full version string (i.e., with timestamp).
    dashboardsVersionEx?: string;

    // E.g., 8.4.10-alpha.1
    dashboardsVersion?: string;

}
```

See also :

- [`JSON5`](../JSON5.md)
- [`Tasks`](../Tasks.md)
- [`Task`](../Task.md)

## Example

```json5
{
  action: "ServerStatus",
  assertions: [
    {
      serverVersion: "8.4.10",
      dashboardsVersion: "8.4.10-alpha.1"
    }
  ]
}
```

Asserting the versions with the full timestamp information :

```json5
{
  action: "ServerStatus",
  assertions: [
    {
      serverVersionEx: "8.4.10+26-Mar-2024 12:46:10 UTC",
      dashboardsVersionEx: "8.4.10-alpha.1+Thu, 14 Mar 2024 12:48:56 GMT",
    }
  ]
}
```

## Java Source Code

For more details and the most current information, please refer to
the [source code](../../../../src/main/java/ic3/analyticsops/test/task/server/AOServerStatusTask.java).

_
